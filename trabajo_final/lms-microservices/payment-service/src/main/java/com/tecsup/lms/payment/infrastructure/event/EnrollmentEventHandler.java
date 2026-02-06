package com.tecsup.lms.payment.infrastructure.event;

import com.tecsup.lms.payment.application.PaymentProcessor;
import com.tecsup.lms.shared.domain.event.PaymentApprovedEvent;
import com.tecsup.lms.shared.domain.event.PaymentRejectedEvent;
import com.tecsup.lms.payment.domain.model.Payment;
import com.tecsup.lms.payment.domain.repository.PaymentRepository;
import com.tecsup.lms.shared.config.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.tecsup.lms.shared.domain.event.EnrollmentCreatedEvent;


import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentEventHandler {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessor paymentProcessor;
    private final KafkaEventPublisher eventPublisher;

    private static final BigDecimal DEFAULT_COURSE_PRICE = new BigDecimal("99.99");

    @Transactional
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            autoCreateTopics = "false",
            dltTopicSuffix = ".dlq",
            include = RuntimeException.class
    )
    @KafkaListener(
            topics = KafkaTopics.ENROLLMENT_EVENTS,
            groupId = "payment-service-group"
    )
    public void handleEnrollmentEvent(EnrollmentCreatedEvent event) {
        log.info("Received enrollment event: {}", event.getEventType());
        processPaymentForEnrollment(event);
    }

    private void processPaymentForEnrollment(EnrollmentCreatedEvent event) {
        Long enrollmentId = event.getEnrollmentId();
        log.info("Processing payment for enrollment: {}", enrollmentId);

        // Check if payment already exists
        if (paymentRepository.existsByEnrollmentId(enrollmentId)) {
            log.warn("Payment already exists for enrollment: {}", enrollmentId);
            return;
        }

        // Create payment record
        Payment payment = Payment.builder()
                .enrollmentId(enrollmentId)
                .amount(DEFAULT_COURSE_PRICE)
                .status(Payment.PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} created for enrollment {}", saved.getId(), enrollmentId);

        // Process payment
        boolean success = paymentProcessor.process(saved);

        if (success) {
            saved.approve();
            paymentRepository.save(saved);

            eventPublisher.publish(new PaymentApprovedEvent(
                    saved.getId(),
                    saved.getEnrollmentId(),
                    saved.getAmount(),
                    saved.getPaidAt()
            ));
        } else {
            saved.reject();
            paymentRepository.save(saved);

            eventPublisher.publish(new PaymentRejectedEvent(
                    saved.getId(),
                    saved.getEnrollmentId(),
                    "Payment declined by payment processor"
            ));
        }
    }

    @DltHandler
    public void handleDlt(
            EnrollmentCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(value = KafkaHeaders.EXCEPTION_MESSAGE, required = false) String errorMessage) {

        log.error("[PAYMENT-DLT] All retries exhausted - Event from topic: {}, offset: {}", topic, offset);
        log.error("[PAYMENT-DLT] Error: {}", errorMessage);
        log.error("[PAYMENT-DLT] Event data: {}", event);

        // In production, store in DLQ table or send alert
    }
}

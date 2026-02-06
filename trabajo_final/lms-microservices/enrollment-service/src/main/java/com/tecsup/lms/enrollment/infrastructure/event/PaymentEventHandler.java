package com.tecsup.lms.enrollment.infrastructure.event;

import com.tecsup.lms.shared.domain.event.EnrollmentCancelledEvent;
import com.tecsup.lms.shared.domain.event.EnrollmentConfirmedEvent;
import com.tecsup.lms.shared.domain.event.PaymentApprovedEvent;
import com.tecsup.lms.shared.domain.event.PaymentRejectedEvent;
import com.tecsup.lms.enrollment.domain.repository.EnrollmentRepository;
import com.tecsup.lms.shared.config.KafkaTopics;
import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final EnrollmentRepository enrollmentRepository;
    private final KafkaEventPublisher eventPublisher;

    @Transactional
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = "enrollment-service-group"
    )
    public void handlePaymentEvent(DomainEvent event) {
        log.info("Received payment event: {}", event.getEventType());

        if (event instanceof PaymentApprovedEvent e) {
            handlePaymentApproved(e);
        } else if (event instanceof PaymentRejectedEvent e) {
            handlePaymentRejected(e);
        }
    }

    private void handlePaymentApproved(PaymentApprovedEvent event) {
        Long enrollmentId = event.getEnrollmentId();
        log.info("Processing payment approval for enrollment: {}", enrollmentId);

        enrollmentRepository.findById(enrollmentId)
                .ifPresent(enrollment -> {
                    enrollment.confirm();
                    enrollmentRepository.save(enrollment);
                    log.info("Enrollment {} confirmed after payment approval", enrollment.getId());

                    eventPublisher.publish(new EnrollmentConfirmedEvent(
                            enrollment.getId(),
                            enrollment.getUserId(),
                            enrollment.getCourseId()
                    ));
                });
    }

    private void handlePaymentRejected(PaymentRejectedEvent event) {
        Long enrollmentId = event.getEnrollmentId();
        String reason = event.getReason();
        log.info("Processing payment rejection for enrollment: {}", enrollmentId);

        enrollmentRepository.findById(enrollmentId)
                .ifPresent(enrollment -> {
                    enrollment.cancel();
                    enrollmentRepository.save(enrollment);
                    log.info("Enrollment {} cancelled due to payment rejection: {}", enrollment.getId(), reason);

                    eventPublisher.publish(new EnrollmentCancelledEvent(
                            enrollment.getId(),
                            enrollment.getUserId(),
                            enrollment.getCourseId(),
                            reason
                    ));
                });
    }
}

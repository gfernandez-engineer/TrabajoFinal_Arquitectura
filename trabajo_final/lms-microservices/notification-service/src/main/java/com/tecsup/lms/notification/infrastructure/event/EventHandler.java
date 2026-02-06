package com.tecsup.lms.notification.infrastructure.event;

import com.tecsup.lms.notification.application.NotificationService;
import com.tecsup.lms.shared.config.KafkaTopics;
import com.tecsup.lms.shared.domain.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.COURSE_EVENTS,
            groupId = "notification-service-course-group"
    )
    public void handleCourseEvent(DomainEvent event) {
        log.info("Received course event: {}", event.getEventType());

        if (event instanceof CoursePublishedEvent e) {
            log.info("New course published: {}", e.getTitle());
            // In production: notify all subscribed users
        } else if (event instanceof CourseCreatedEvent e) {
            log.info("New course created: {}", e.getTitle());
        }
    }

    @KafkaListener(
            topics = KafkaTopics.ENROLLMENT_EVENTS,
            groupId = "notification-service-enrollment-group"
    )
    public void handleEnrollmentEvent(DomainEvent event) {
        log.info("Received enrollment event: {}", event.getEventType());

        if (event instanceof EnrollmentCreatedEvent e) {
            notificationService.createAndSend(e.getUserId(),
                    String.format("Your enrollment for '%s' is pending payment. Please complete payment to confirm.",
                            e.getCourseTitle()));

        } else if (event instanceof EnrollmentConfirmedEvent e) {
            notificationService.createAndSend(e.getUserId(),
                    String.format("Welcome! Your enrollment for course %d has been confirmed. You can now access the course content.",
                            e.getCourseId()));

        } else if (event instanceof EnrollmentCancelledEvent e) {
            notificationService.createAndSend(e.getUserId(),
                    String.format("Your enrollment for course %d was cancelled. Reason: %s",
                            e.getCourseId(), e.getReason()));
        }
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = "notification-service-payment-group"
    )
    public void handlePaymentEvent(DomainEvent event) {
        log.info("Received payment event: {}", event.getEventType());

        if (event instanceof PaymentApprovedEvent e) {
            log.info("Payment approved for enrollment {}: ${}", e.getEnrollmentId(), e.getAmount());
            // User notification handled by EnrollmentConfirmedEvent

        } else if (event instanceof PaymentRejectedEvent e) {
            log.warn("Payment rejected for enrollment {}: {}", e.getEnrollmentId(), e.getReason());
            // User notification handled by EnrollmentCancelledEvent
        }
    }
}

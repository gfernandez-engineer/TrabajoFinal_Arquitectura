package com.tecsup.lms.notification.application;

import com.tecsup.lms.notification.domain.model.Notification;
import com.tecsup.lms.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createAndSend(Long userId, String message) {
        log.info("Creating notification for user {}: {}", userId, message);

        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .sent(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // Simulate sending (email, push, etc.)
        sendNotification(saved);

        saved.markAsSent();
        notificationRepository.save(saved);

        log.info("Notification {} sent to user {}", saved.getId(), userId);

        return saved;
    }

    private void sendNotification(Notification notification) {
        // In production, integrate with email service, push notifications, etc.
        log.info("[EMAIL] Sending to user {}: {}", notification.getUserId(), notification.getMessage());

        // Simulate sending delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

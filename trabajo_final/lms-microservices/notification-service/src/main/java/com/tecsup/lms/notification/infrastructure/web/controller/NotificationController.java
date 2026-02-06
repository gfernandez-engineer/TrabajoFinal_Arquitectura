package com.tecsup.lms.notification.infrastructure.web.controller;

import com.tecsup.lms.notification.domain.repository.NotificationRepository;
import com.tecsup.lms.notification.infrastructure.web.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> notifications = notificationRepository.findAll().stream()
                .map(NotificationResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(notification -> ResponseEntity.ok(NotificationResponse.fromEntity(notification)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUser(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationRepository.findByUserId(userId).stream()
                .map(NotificationResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<NotificationResponse>> getPendingNotifications() {
        List<NotificationResponse> notifications = notificationRepository.findBySentFalse().stream()
                .map(NotificationResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(notifications);
    }
}

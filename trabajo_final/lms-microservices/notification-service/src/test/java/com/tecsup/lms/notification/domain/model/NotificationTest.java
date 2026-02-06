package com.tecsup.lms.notification.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void shouldCreateNotificationAsNotSent() {
        Notification notification = Notification.builder()
                .userId(1L)
                .message("Test notification message")
                .build();

        assertEquals(1L, notification.getUserId());
        assertEquals("Test notification message", notification.getMessage());
        assertFalse(notification.getSent());
    }

    @Test
    void shouldMarkNotificationAsSent() {
        Notification notification = Notification.builder()
                .userId(1L)
                .message("Test notification message")
                .build();

        notification.markAsSent();

        assertTrue(notification.getSent());
    }

    @Test
    void shouldHandleLongMessage() {
        String longMessage = "A".repeat(500);

        Notification notification = Notification.builder()
                .userId(1L)
                .message(longMessage)
                .build();

        assertEquals(500, notification.getMessage().length());
    }

    @Test
    void shouldAllowUpdatingNotificationFields() {
        Notification notification = Notification.builder()
                .userId(1L)
                .message("Original message")
                .build();

        notification.setMessage("Updated message");

        assertEquals("Updated message", notification.getMessage());
    }
}

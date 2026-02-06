package com.tecsup.lms.notification.application;

import com.tecsup.lms.notification.domain.model.Notification;
import com.tecsup.lms.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .id(1L)
                .userId(1L)
                .message("Test notification")
                .sent(false)
                .build();
    }

    @Test
    void shouldCreateAndSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.createAndSend(1L, "Test notification");

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Test notification", result.getMessage());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void shouldCreateNotificationWithCorrectUserId() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.createAndSend(1L, "Welcome message");

        assertEquals(1L, result.getUserId());
    }

    @Test
    void shouldCreateNotificationWithCorrectMessage() {
        String expectedMessage = "Your enrollment has been confirmed";
        Notification notification = Notification.builder()
                .id(2L)
                .userId(1L)
                .message(expectedMessage)
                .sent(false)
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createAndSend(1L, expectedMessage);

        assertEquals(expectedMessage, result.getMessage());
    }
}

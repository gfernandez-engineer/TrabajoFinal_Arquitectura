package com.tecsup.lms.enrollment.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {

    @Test
    void shouldCreateEnrollmentWithPendingPaymentStatus() {
        Enrollment enrollment = Enrollment.builder()
                .userId(1L)
                .courseId(1L)
                .build();

        assertEquals(1L, enrollment.getUserId());
        assertEquals(1L, enrollment.getCourseId());
        assertEquals(Enrollment.EnrollmentStatus.PENDING_PAYMENT, enrollment.getStatus());
    }

    @Test
    void shouldConfirmEnrollment() {
        Enrollment enrollment = Enrollment.builder()
                .userId(1L)
                .courseId(1L)
                .build();

        enrollment.confirm();

        assertEquals(Enrollment.EnrollmentStatus.CONFIRMED, enrollment.getStatus());
        assertNotNull(enrollment.getUpdatedAt());
    }

    @Test
    void shouldCancelEnrollment() {
        Enrollment enrollment = Enrollment.builder()
                .userId(1L)
                .courseId(1L)
                .build();

        enrollment.cancel();

        assertEquals(Enrollment.EnrollmentStatus.CANCELLED, enrollment.getStatus());
        assertNotNull(enrollment.getUpdatedAt());
    }

    @Test
    void shouldCheckIfEnrollmentIsPendingPayment() {
        Enrollment pendingEnrollment = Enrollment.builder()
                .userId(1L)
                .courseId(1L)
                .build();

        Enrollment confirmedEnrollment = Enrollment.builder()
                .userId(2L)
                .courseId(1L)
                .status(Enrollment.EnrollmentStatus.CONFIRMED)
                .build();

        assertTrue(pendingEnrollment.isPendingPayment());
        assertFalse(confirmedEnrollment.isPendingPayment());
    }

    @Test
    void shouldHaveCorrectEnumValues() {
        assertEquals(3, Enrollment.EnrollmentStatus.values().length);
        assertEquals(Enrollment.EnrollmentStatus.PENDING_PAYMENT, Enrollment.EnrollmentStatus.valueOf("PENDING_PAYMENT"));
        assertEquals(Enrollment.EnrollmentStatus.CONFIRMED, Enrollment.EnrollmentStatus.valueOf("CONFIRMED"));
        assertEquals(Enrollment.EnrollmentStatus.CANCELLED, Enrollment.EnrollmentStatus.valueOf("CANCELLED"));
    }
}

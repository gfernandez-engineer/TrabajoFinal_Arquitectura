package com.tecsup.lms.payment.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void shouldCreatePaymentWithPendingStatus() {
        Payment payment = Payment.builder()
                .enrollmentId(1L)
                .amount(new BigDecimal("99.99"))
                .build();

        assertEquals(1L, payment.getEnrollmentId());
        assertEquals(new BigDecimal("99.99"), payment.getAmount());
        assertEquals(Payment.PaymentStatus.PENDING, payment.getStatus());
        // createdAt is set by @PrePersist, not available in unit test
    }

    @Test
    void shouldApprovePayment() {
        Payment payment = Payment.builder()
                .enrollmentId(1L)
                .amount(new BigDecimal("99.99"))
                .build();

        payment.approve();

        assertEquals(Payment.PaymentStatus.APPROVED, payment.getStatus());
        assertNotNull(payment.getPaidAt());
    }

    @Test
    void shouldRejectPayment() {
        Payment payment = Payment.builder()
                .enrollmentId(1L)
                .amount(new BigDecimal("99.99"))
                .build();

        payment.reject();

        assertEquals(Payment.PaymentStatus.REJECTED, payment.getStatus());
        assertNull(payment.getPaidAt());
    }

    @Test
    void shouldCheckIfPaymentIsPending() {
        Payment pendingPayment = Payment.builder()
                .enrollmentId(1L)
                .amount(new BigDecimal("99.99"))
                .build();

        Payment approvedPayment = Payment.builder()
                .enrollmentId(2L)
                .amount(new BigDecimal("99.99"))
                .status(Payment.PaymentStatus.APPROVED)
                .build();

        assertTrue(pendingPayment.isPending());
        assertFalse(approvedPayment.isPending());
    }
}

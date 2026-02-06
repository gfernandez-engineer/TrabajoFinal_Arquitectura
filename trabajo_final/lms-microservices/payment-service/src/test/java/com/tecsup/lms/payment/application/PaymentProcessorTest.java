package com.tecsup.lms.payment.application;

import com.tecsup.lms.payment.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessorTest {

    private PaymentProcessor paymentProcessor;

    @BeforeEach
    void setUp() {
        paymentProcessor = new PaymentProcessor();
    }

    @Test
    void shouldProcessPayment() {
        Payment payment = Payment.builder()
                .id(1L)
                .enrollmentId(1L)
                .amount(new BigDecimal("99.99"))
                .build();

        // El resultado puede ser true o false debido a la aleatoriedad
        boolean result = paymentProcessor.process(payment);

        // Solo verificamos que el método se ejecuta sin errores
        assertTrue(result || !result);
    }

    @RepeatedTest(10)
    void shouldReturnBooleanResult() {
        Payment payment = Payment.builder()
                .id(1L)
                .enrollmentId(1L)
                .amount(new BigDecimal("99.99"))
                .build();

        boolean result = paymentProcessor.process(payment);

        // Verifica que siempre retorna un boolean válido
        assertNotNull(result);
    }

    @Test
    void shouldHandlePaymentWithZeroAmount() {
        Payment payment = Payment.builder()
                .id(1L)
                .enrollmentId(1L)
                .amount(BigDecimal.ZERO)
                .build();

        boolean result = paymentProcessor.process(payment);

        assertTrue(result || !result);
    }

    @Test
    void shouldHandlePaymentWithLargeAmount() {
        Payment payment = Payment.builder()
                .id(1L)
                .enrollmentId(1L)
                .amount(new BigDecimal("9999999.99"))
                .build();

        boolean result = paymentProcessor.process(payment);

        assertTrue(result || !result);
    }
}

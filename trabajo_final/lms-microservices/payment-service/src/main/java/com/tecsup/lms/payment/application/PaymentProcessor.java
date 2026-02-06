package com.tecsup.lms.payment.application;

import com.tecsup.lms.payment.domain.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class PaymentProcessor {

    private final Random random = new Random();

    /**
     * Simulates payment processing with external payment gateway.
     * In production, this would integrate with Stripe, PayPal, etc.
     *
     * @param payment The payment to process
     * @return true if payment is successful, false otherwise
     */
    public boolean process(Payment payment) {
        log.info("Processing payment {} for enrollment {} - Amount: {}",
                payment.getId(), payment.getEnrollmentId(), payment.getAmount());

        // Simulate processing time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate 100% success rate for testing (change to < 80 for 80% success)
        boolean success = random.nextInt(100) < 80;

        if (success) {
            log.info("Payment {} approved", payment.getId());
        } else {
            log.warn("Payment {} rejected", payment.getId());
        }

        return success;
    }
}

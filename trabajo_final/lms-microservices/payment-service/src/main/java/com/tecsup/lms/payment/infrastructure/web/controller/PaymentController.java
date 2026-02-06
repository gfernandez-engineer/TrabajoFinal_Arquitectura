package com.tecsup.lms.payment.infrastructure.web.controller;

import com.tecsup.lms.payment.domain.repository.PaymentRepository;
import com.tecsup.lms.payment.infrastructure.web.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(payment -> ResponseEntity.ok(PaymentResponse.fromEntity(payment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<PaymentResponse> getPaymentByEnrollment(@PathVariable Long enrollmentId) {
        return paymentRepository.findByEnrollmentId(enrollmentId)
                .map(payment -> ResponseEntity.ok(PaymentResponse.fromEntity(payment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentRepository.findAll().stream()
                .map(PaymentResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(payments);
    }
}

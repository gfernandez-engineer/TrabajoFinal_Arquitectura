package com.tecsup.lms.payment.domain.repository;

import com.tecsup.lms.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentId(Long enrollmentId);
}

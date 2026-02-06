package com.tecsup.lms.enrollment.infrastructure.web.controller;

import com.tecsup.lms.enrollment.application.EnrollStudentUseCase;
import com.tecsup.lms.enrollment.domain.repository.EnrollmentRepository;
import com.tecsup.lms.enrollment.infrastructure.web.dto.EnrollmentRequest;
import com.tecsup.lms.enrollment.infrastructure.web.dto.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollStudentUseCase enrollStudentUseCase;
    private final EnrollmentRepository enrollmentRepository;

    @PostMapping
    public ResponseEntity<?> enrollStudent(@RequestBody EnrollmentRequest request) {
        log.info("Enrollment request received for user {} in course {}", request.getUserId(), request.getCourseId());

        try {
            EnrollmentResponse response = enrollStudentUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Enrollment failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollment(@PathVariable Long id) {
        return enrollmentRepository.findById(id)
                .map(enrollment -> ResponseEntity.ok(EnrollmentResponse.fromEntity(enrollment)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        List<EnrollmentResponse> enrollments = enrollmentRepository.findAll().stream()
                .map(EnrollmentResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByUser(@PathVariable Long userId) {
        List<EnrollmentResponse> enrollments = enrollmentRepository.findByUserId(userId).stream()
                .map(EnrollmentResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        List<EnrollmentResponse> enrollments = enrollmentRepository.findByCourseId(courseId).stream()
                .map(EnrollmentResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(enrollments);
    }
}

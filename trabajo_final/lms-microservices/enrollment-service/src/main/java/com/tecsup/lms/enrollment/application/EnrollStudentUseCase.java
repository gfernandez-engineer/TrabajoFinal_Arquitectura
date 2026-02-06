package com.tecsup.lms.enrollment.application;

import com.tecsup.lms.shared.domain.event.EnrollmentCreatedEvent;
import com.tecsup.lms.enrollment.domain.model.Enrollment;
import com.tecsup.lms.enrollment.domain.repository.EnrollmentRepository;
import com.tecsup.lms.enrollment.infrastructure.client.CourseServiceClient;
import com.tecsup.lms.enrollment.infrastructure.client.UserServiceClient;
import com.tecsup.lms.enrollment.infrastructure.event.KafkaEventPublisher;
import com.tecsup.lms.enrollment.infrastructure.web.dto.EnrollmentRequest;
import com.tecsup.lms.enrollment.infrastructure.web.dto.EnrollmentResponse;
import com.tecsup.lms.shared.dto.CourseValidationResponse;
import com.tecsup.lms.shared.dto.UserValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollStudentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final UserServiceClient userServiceClient;
    private final CourseServiceClient courseServiceClient;
    private final KafkaEventPublisher eventPublisher;

    @Transactional
    public EnrollmentResponse execute(EnrollmentRequest request) {
        log.info("Processing enrollment for user {} in course {}", request.getUserId(), request.getCourseId());

        // 1. Validate user exists and is active
        UserValidationResponse userValidation = userServiceClient.validateUser(request.getUserId());
        if (!userValidation.isValid()) {
            throw new IllegalArgumentException("Invalid user: " + userValidation.getMessage());
        }

        // 2. Validate course exists and is published
        CourseValidationResponse courseValidation = courseServiceClient.validateCourse(request.getCourseId());
        if (!courseValidation.isExists()) {
            throw new IllegalArgumentException("Course not found: " + request.getCourseId());
        }
        if (!courseValidation.isPublished()) {
            throw new IllegalArgumentException("Course is not published: " + courseValidation.getTitle());
        }

        // 3. Check for duplicate enrollment
        if (enrollmentRepository.existsByUserIdAndCourseId(request.getUserId(), request.getCourseId())) {
            throw new IllegalArgumentException("User is already enrolled in this course");
        }

        // 4. Create enrollment with PENDING_PAYMENT status
        Enrollment enrollment = Enrollment.builder()
                .userId(request.getUserId())
                .courseId(request.getCourseId())
                .status(Enrollment.EnrollmentStatus.PENDING_PAYMENT)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrollment created: {} with status PENDING_PAYMENT", saved.getId());

        // 5. Publish enrollment event to Kafka
        eventPublisher.publish(new EnrollmentCreatedEvent(
                saved.getId(),
                saved.getUserId(),
                saved.getCourseId(),
                courseValidation.getTitle(),
                userValidation.getEmail()
        ));

        return EnrollmentResponse.fromEntity(saved);
    }
}

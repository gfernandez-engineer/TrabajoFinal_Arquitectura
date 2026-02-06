package com.tecsup.lms.enrollment.application;

import com.tecsup.lms.enrollment.domain.model.Enrollment;
import com.tecsup.lms.enrollment.domain.repository.EnrollmentRepository;
import com.tecsup.lms.enrollment.infrastructure.client.CourseServiceClient;
import com.tecsup.lms.enrollment.infrastructure.client.UserServiceClient;
import com.tecsup.lms.enrollment.infrastructure.event.KafkaEventPublisher;
import com.tecsup.lms.enrollment.infrastructure.web.dto.EnrollmentRequest;
import com.tecsup.lms.enrollment.infrastructure.web.dto.EnrollmentResponse;
import com.tecsup.lms.shared.dto.CourseValidationResponse;
import com.tecsup.lms.shared.dto.UserValidationResponse;
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
class EnrollStudentUseCaseTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CourseServiceClient courseServiceClient;

    @Mock
    private KafkaEventPublisher eventPublisher;

    @InjectMocks
    private EnrollStudentUseCase enrollStudentUseCase;

    private Enrollment testEnrollment;
    private EnrollmentRequest testRequest;

    @BeforeEach
    void setUp() {
        testEnrollment = Enrollment.builder()
                .id(1L)
                .userId(1L)
                .courseId(1L)
                .status(Enrollment.EnrollmentStatus.PENDING_PAYMENT)
                .build();

        testRequest = new EnrollmentRequest();
        testRequest.setUserId(1L);
        testRequest.setCourseId(1L);
    }

    @Test
    void shouldEnrollStudentSuccessfully() {
        UserValidationResponse userValidation = UserValidationResponse.valid(1L, "Juan Perez", "juan@test.com");
        CourseValidationResponse courseValidation = CourseValidationResponse.valid(1L, "Java Avanzado");

        when(userServiceClient.validateUser(1L)).thenReturn(userValidation);
        when(courseServiceClient.validateCourse(1L)).thenReturn(courseValidation);
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        EnrollmentResponse result = enrollStudentUseCase.execute(testRequest);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getCourseId());
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotValid() {
        UserValidationResponse userValidation = UserValidationResponse.invalid("User not found");

        when(userServiceClient.validateUser(99L)).thenReturn(userValidation);

        testRequest.setUserId(99L);

        assertThrows(IllegalArgumentException.class, () -> {
            enrollStudentUseCase.execute(testRequest);
        });

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        UserValidationResponse userValidation = UserValidationResponse.valid(1L, "Juan Perez", "juan@test.com");
        CourseValidationResponse courseValidation = CourseValidationResponse.notFound(99L);

        when(userServiceClient.validateUser(1L)).thenReturn(userValidation);
        when(courseServiceClient.validateCourse(99L)).thenReturn(courseValidation);

        testRequest.setCourseId(99L);

        assertThrows(IllegalArgumentException.class, () -> {
            enrollStudentUseCase.execute(testRequest);
        });

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCourseNotPublished() {
        UserValidationResponse userValidation = UserValidationResponse.valid(1L, "Juan Perez", "juan@test.com");
        CourseValidationResponse courseValidation = CourseValidationResponse.notPublished(1L, "Java Basico");

        when(userServiceClient.validateUser(1L)).thenReturn(userValidation);
        when(courseServiceClient.validateCourse(1L)).thenReturn(courseValidation);

        assertThrows(IllegalArgumentException.class, () -> {
            enrollStudentUseCase.execute(testRequest);
        });

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAlreadyEnrolled() {
        UserValidationResponse userValidation = UserValidationResponse.valid(1L, "Juan Perez", "juan@test.com");
        CourseValidationResponse courseValidation = CourseValidationResponse.valid(1L, "Java Avanzado");

        when(userServiceClient.validateUser(1L)).thenReturn(userValidation);
        when(courseServiceClient.validateCourse(1L)).thenReturn(courseValidation);
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            enrollStudentUseCase.execute(testRequest);
        });

        verify(enrollmentRepository, never()).save(any());
    }
}

package com.tecsup.lms.enrollment.infrastructure.web.dto;

import com.tecsup.lms.enrollment.domain.model.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    private Long userId;
    private Long courseId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EnrollmentResponse fromEntity(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUserId())
                .courseId(enrollment.getCourseId())
                .status(enrollment.getStatus().name())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}

package com.tecsup.lms.enrollment.infrastructure.web.dto;

import lombok.Data;

@Data
public class EnrollmentRequest {
    private Long userId;
    private Long courseId;
}

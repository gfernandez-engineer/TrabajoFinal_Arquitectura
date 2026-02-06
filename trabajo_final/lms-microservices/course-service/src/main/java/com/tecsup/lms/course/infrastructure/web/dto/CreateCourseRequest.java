package com.tecsup.lms.course.infrastructure.web.dto;

import lombok.Data;

@Data
public class CreateCourseRequest {
    private String title;
    private String description;
    private String instructor;
}

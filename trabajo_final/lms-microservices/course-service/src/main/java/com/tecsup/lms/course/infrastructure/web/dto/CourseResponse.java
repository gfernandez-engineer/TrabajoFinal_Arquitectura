package com.tecsup.lms.course.infrastructure.web.dto;

import com.tecsup.lms.course.domain.model.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String instructor;
    private Boolean published;
    private LocalDateTime createdAt;

    public static CourseResponse fromEntity(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .instructor(course.getInstructor())
                .published(course.getPublished())
                .createdAt(course.getCreatedAt())
                .build();
    }
}

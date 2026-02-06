package com.tecsup.lms.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseValidationResponse {

    private Long courseId;
    private String title;
    private boolean published;
    private boolean exists;
    private String message;

    public static CourseValidationResponse valid(Long courseId, String title) {
        return CourseValidationResponse.builder()
                .courseId(courseId)
                .title(title)
                .published(true)
                .exists(true)
                .build();
    }

    public static CourseValidationResponse notFound(Long courseId) {
        return CourseValidationResponse.builder()
                .courseId(courseId)
                .exists(false)
                .message("Course not found")
                .build();
    }

    public static CourseValidationResponse notPublished(Long courseId, String title) {
        return CourseValidationResponse.builder()
                .courseId(courseId)
                .title(title)
                .exists(true)
                .published(false)
                .message("Course is not published")
                .build();
    }
}

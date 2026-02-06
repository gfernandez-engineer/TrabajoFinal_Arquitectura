package com.tecsup.lms.enrollment.infrastructure.client;

import com.tecsup.lms.shared.dto.CourseValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.course.url}")
    private String courseServiceUrl;

    public CourseValidationResponse validateCourse(Long courseId) {
        try {
            log.info("Validating course {} at {}", courseId, courseServiceUrl);

            CourseValidationResponse response = restTemplate.getForObject(
                    courseServiceUrl + "/api/courses/" + courseId + "/validate",
                    CourseValidationResponse.class
            );

            log.info("Course validation response: {}", response);
            return response;

        } catch (RestClientException e) {
            log.error("Error calling course-service: {}", e.getMessage());
            return CourseValidationResponse.builder()
                    .courseId(courseId)
                    .exists(false)
                    .message("Course service unavailable: " + e.getMessage())
                    .build();
        }
    }
}

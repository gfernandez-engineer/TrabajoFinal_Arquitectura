package com.tecsup.lms.course.infrastructure.web.controller;

import com.tecsup.lms.shared.domain.event.CourseCreatedEvent;
import com.tecsup.lms.shared.domain.event.CoursePublishedEvent;
import com.tecsup.lms.course.domain.model.Course;
import com.tecsup.lms.course.domain.repository.CourseRepository;
import com.tecsup.lms.course.infrastructure.event.KafkaEventPublisher;
import com.tecsup.lms.course.infrastructure.web.dto.CourseResponse;
import com.tecsup.lms.course.infrastructure.web.dto.CreateCourseRequest;
import com.tecsup.lms.shared.dto.CourseValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepository;
    private final KafkaEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CreateCourseRequest request) {
        log.info("Creating course: {}", request.getTitle());

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(request.getInstructor())
                .published(false)
                .build();

        Course saved = courseRepository.save(course);
        log.info("Course created with ID: {}", saved.getId());

        // Publish event
        eventPublisher.publish(new CourseCreatedEvent(
                saved.getId(),
                saved.getTitle(),
                saved.getInstructor()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CourseResponse.fromEntity(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id) {
        return courseRepository.findById(id)
                .map(course -> ResponseEntity.ok(CourseResponse.fromEntity(course)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<CourseValidationResponse> validateCourse(@PathVariable Long id) {
        log.info("Validating course: {}", id);

        return courseRepository.findById(id)
                .map(course -> {
                    if (course.isPublished()) {
                        return ResponseEntity.ok(
                                CourseValidationResponse.valid(course.getId(), course.getTitle())
                        );
                    } else {
                        return ResponseEntity.ok(
                                CourseValidationResponse.notPublished(course.getId(), course.getTitle())
                        );
                    }
                })
                .orElse(ResponseEntity.ok(
                        CourseValidationResponse.notFound(id)
                ));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable Long id) {
        log.info("Publishing course: {}", id);

        return courseRepository.findById(id)
                .map(course -> {
                    course.publish();
                    Course saved = courseRepository.save(course);
                    log.info("Course published: {}", saved.getId());

                    // Publish event
                    eventPublisher.publish(new CoursePublishedEvent(
                            saved.getId(),
                            saved.getTitle()
                    ));

                    return ResponseEntity.ok(CourseResponse.fromEntity(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseRepository.findAll().stream()
                .map(CourseResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/published")
    public ResponseEntity<List<CourseResponse>> getPublishedCourses() {
        List<CourseResponse> courses = courseRepository.findByPublishedTrue().stream()
                .map(CourseResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(courses);
    }
}

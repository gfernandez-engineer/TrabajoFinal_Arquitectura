package com.tecsup.lms.course.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void shouldCreateCourseAsUnpublished() {
        Course course = Course.builder()
                .title("Java Avanzado")
                .description("Curso completo de Java")
                .instructor("Ana Garcia")
                .build();

        assertEquals("Java Avanzado", course.getTitle());
        assertEquals("Curso completo de Java", course.getDescription());
        assertEquals("Ana Garcia", course.getInstructor());
        assertFalse(course.isPublished());
    }

    @Test
    void shouldPublishCourse() {
        Course course = Course.builder()
                .title("Java Avanzado")
                .description("Curso completo de Java")
                .instructor("Ana Garcia")
                .build();

        course.publish();

        assertTrue(course.isPublished());
    }

    @Test
    void shouldCreatePublishedCourse() {
        Course course = Course.builder()
                .title("Java Avanzado")
                .description("Curso completo de Java")
                .instructor("Ana Garcia")
                .published(true)
                .build();

        assertTrue(course.isPublished());
    }

    @Test
    void shouldAllowUpdatingCourseFields() {
        Course course = Course.builder()
                .title("Java Basico")
                .description("Curso basico")
                .instructor("Ana Garcia")
                .build();

        course.setTitle("Java Avanzado");
        course.setDescription("Curso avanzado de Java");
        course.setInstructor("Carlos Lopez");

        assertEquals("Java Avanzado", course.getTitle());
        assertEquals("Curso avanzado de Java", course.getDescription());
        assertEquals("Carlos Lopez", course.getInstructor());
    }
}

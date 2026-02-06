package com.tecsup.lms.course.application;

import com.tecsup.lms.course.domain.model.Course;
import com.tecsup.lms.course.domain.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRepositoryTest {

    @Mock
    private CourseRepository courseRepository;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = Course.builder()
                .id(1L)
                .title("Java Avanzado")
                .description("Curso completo de Java")
                .instructor("Ana Garcia")
                .published(false)
                .build();
    }

    @Test
    void shouldSaveCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseRepository.save(testCourse);

        assertNotNull(result);
        assertEquals("Java Avanzado", result.getTitle());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void shouldFindCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        Optional<Course> result = courseRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Java Avanzado", result.get().getTitle());
    }

    @Test
    void shouldReturnEmptyWhenCourseNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Course> result = courseRepository.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindAllCourses() {
        Course course2 = Course.builder()
                .id(2L)
                .title("Python Basico")
                .description("Curso de Python")
                .instructor("Carlos Lopez")
                .build();

        when(courseRepository.findAll()).thenReturn(Arrays.asList(testCourse, course2));

        List<Course> result = courseRepository.findAll();

        assertEquals(2, result.size());
    }
}

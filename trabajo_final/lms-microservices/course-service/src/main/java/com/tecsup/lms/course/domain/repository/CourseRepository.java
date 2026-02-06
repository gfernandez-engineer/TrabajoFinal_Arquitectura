package com.tecsup.lms.course.domain.repository;

import com.tecsup.lms.course.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByPublishedTrue();
}

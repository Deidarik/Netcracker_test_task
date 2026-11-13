package com.example.lmslite.repository;

import com.example.lmslite.model.Course;
import com.example.lmslite.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository
        extends JpaRepository<Course, Long> {

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByAuthorId(Long authorId);

    List<Course> findByTitleContainingIgnoreCase(String title);
}

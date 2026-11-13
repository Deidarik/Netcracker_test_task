package com.example.lmslite.repository;

import com.example.lmslite.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(
            Long enrollmentId, Long lessonId
    );

    boolean existsByLessonId(Long lessonId);
}

package com.example.lmslite.dto;

import com.example.lmslite.enums.EnrollmentStatus;

import java.util.List;

public record ProgressResponse(
        Long enrollmentId,
        String courseTitle,
        int progressPercent,
        int completedLessons,
        int totalLessons,
        EnrollmentStatus status,
        List<LessonItem> lessons
) {
    public record LessonItem(
            Long id,
            String title,
            boolean completed
    ) {
    }
}
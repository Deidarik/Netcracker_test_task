package com.example.lmslite.dto;

import com.example.lmslite.enums.CourseStatus;
import java.util.List;

public record CourseDetailsResponse(
        Long id,
        String title,
        String description,
        String authorName,
        CourseStatus status,
        int totalLessons,
        int duration,
        List<LessonResponse> lessons
) {
}
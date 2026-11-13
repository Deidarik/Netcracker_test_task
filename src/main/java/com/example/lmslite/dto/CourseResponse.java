package com.example.lmslite.dto;

import com.example.lmslite.enums.CourseStatus;

public record CourseResponse(
        Long id,
        String title,
        String description,
        String authorName,
        CourseStatus status,
        int totalLessons,
        int duration
) {
}
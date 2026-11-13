package com.example.lmslite.dto;

import com.example.lmslite.enums.LessonType;

public record LessonResponse(
        Long id,
        String title,
        LessonType type,
        int orderIndex
) {
}
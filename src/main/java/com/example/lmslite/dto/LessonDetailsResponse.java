
package com.example.lmslite.dto;

import com.example.lmslite.enums.LessonType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LessonDetailsResponse(
        Long id,
        String title,
        LessonType type,
        int orderIndex,

        // Для VIDEO
        String videoUrl,

        // Для TEXT
        String content,

        // Для QUIZ
        Integer passingScore,
        List<QuestionResponse> questions
) {
}

package com.example.lmslite.dto;

public record QuizResultResponse(
        Long quizId,
        String quizTitle,
        int totalQuestions,
        int correctAnswers,
        int scorePercent,
        boolean passed,
        int passingScore
) {
}
package com.example.lmslite.dto;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        List<String> options
) {
}
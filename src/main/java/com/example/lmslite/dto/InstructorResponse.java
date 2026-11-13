package com.example.lmslite.dto;

public record InstructorResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String bio
) {
}
package com.example.lmslite.dto;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
package com.example.lmslite.dto;

import com.example.lmslite.enums.EnrollmentStatus;
import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        Long courseId,
        EnrollmentStatus status,
        LocalDateTime enrolledAt
) {
}
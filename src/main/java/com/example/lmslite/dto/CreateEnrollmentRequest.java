package com.example.lmslite.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(

        @NotNull(message = "ID студента обязателен")
        @Min(value = 1,
                message = "ID студента должен быть положительным")
        Long studentId,

        @NotNull(message = "ID курса обязателен")
        @Min(value = 1,
                message = "ID курса должен быть положительным")
        Long courseId

) {
}

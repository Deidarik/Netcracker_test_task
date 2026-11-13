
package com.example.lmslite.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCourseRequest(

        @NotBlank(message = "Название курса обязательно")
        @Size(min = 3, max = 150,
                message = "Название курса должно содержать от 3 до 150 символов")
        String title,

        @Size(max = 2000,
                message = "Описание курса не должно превышать 2000 символов")
        String description,

        @NotNull(message = "ID преподавателя обязателен")
        @Min(value = 1,
                message = "ID преподавателя должен быть положительным")
        Long instructorId

) {
}

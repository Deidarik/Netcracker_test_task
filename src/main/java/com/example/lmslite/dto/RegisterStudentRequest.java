package com.example.lmslite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterStudentRequest(

        @NotBlank(message = "Имя обязательно")
        @Size(min = 2, max = 50,
                message = "Имя должно содержать от 2 до 50 символов")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(min = 2, max = 50,
                message = "Фамилия должна содержать от 2 до 50 символов")
        String lastName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный формат email")
        @Size(max = 120, message = "Email слишком длинный")
        String email

) {
}
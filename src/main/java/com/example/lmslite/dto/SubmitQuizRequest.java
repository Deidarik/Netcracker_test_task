package com.example.lmslite.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SubmitQuizRequest(

        @NotNull(message = "Список ответов обязателен")
        @Size(min = 1, message = "Нужно передать хотя бы один ответ")
        @Valid
        List<
                @NotNull(message = "Ответ не может быть null")
                        Answer
                > answers

) {

    public record Answer(

            @NotNull(message = "ID вопроса обязателен")
            @Min(value = 1,
                    message = "ID вопроса должен быть положительным")
            Long questionId,

            @NotNull(message = "Индекс ответа обязателен")
            @Min(value = 0,
                    message = "Индекс ответа не может быть отрицательным")
            Integer selectedOption

    ) {
    }
}

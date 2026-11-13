
package com.example.lmslite.dto;

import com.example.lmslite.enums.LessonType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateLessonRequest(

        @NotBlank(message = "Название урока обязательно")
        @Size(min = 3, max = 150,
                message = "Название должно содержать от 3 до 150 символов")
        String title,

        @NotNull(message = "Тип урока обязателен")
        LessonType type,

        @NotNull(message = "Порядковый номер обязателен")
        @Min(value = 0, message = "Порядковый номер не может быть отрицательным")
        Integer orderIndex,

        // Для VIDEO
        @Size(max = 2000, message = "Ссылка на видео слишком длинная")
        String videoUrl,

        // Для TEXT
        @Size(max = 20000, message = "Текст урока слишком длинный")
        String content,

        // Для QUIZ
        @Valid
        List<QuestionRequest> questions,

        @Min(value = 0, message = "Проходной балл не может быть меньше 0")
        @Max(value = 100, message = "Проходной балл не может быть больше 100")
        Integer passingScore

) {

    public record QuestionRequest(

            @NotBlank(message = "Текст вопроса обязателен")
            @Size(min = 3, max = 1000,
                    message = "Текст вопроса должен содержать от 3 до 1000 символов")
            String text,

            @NotNull(message = "Варианты ответов обязательны")
            @Size(min = 2, max = 10,
                    message = "Должно быть от 2 до 10 вариантов ответа")
            List<
                    @NotBlank(message = "Вариант ответа не может быть пустым")
                    @Size(max = 500, message = "Вариант ответа слишком длинный")
                            String
                    > options,

            @NotNull(message = "Правильный ответ обязателен")
            @Min(value = 0, message = "Индекс ответа не может быть отрицательным")
            Integer correctOptionIndex

    ) {

        @AssertTrue(message = "Индекс правильного ответа выходит за пределы списка")
        public boolean isCorrectOptionIndexValid() {
            return correctOptionIndex == null
                    || options == null
                    || correctOptionIndex < options.size();
        }
    }


    @AssertTrue(message = "Для VIDEO необходимо указать videoUrl")
    public boolean isVideoDataValid() {
        return type != LessonType.VIDEO
                || (videoUrl != null && !videoUrl.isBlank());
    }

    @AssertTrue(message = "Для TEXT необходимо указать content")
    public boolean isTextDataValid() {
        return type != LessonType.TEXT
                || (content != null && !content.isBlank());
    }

    @AssertTrue(message = "Для QUIZ необходимы вопросы и проходной балл")
    public boolean isQuizDataValid() {
        return type != LessonType.QUIZ
                || (passingScore != null
                && questions != null
                && !questions.isEmpty());
    }
}

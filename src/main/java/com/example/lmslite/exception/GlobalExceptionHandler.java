
package com.example.lmslite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CourseNotFoundException.class,
            StudentNotFoundException.class,
            InstructorNotFoundException.class,
            LessonNotFoundException.class,
            CertificateNotFoundException.class
    })
    public ProblemDetail handleNotFound(RuntimeException ex) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(AlreadyEnrolledException.class)
    public ProblemDetail handleAlreadyEnrolled(
            AlreadyEnrolledException ex
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler({
            IllegalStateException.class,
            IllegalArgumentException.class,
            CourseNotPublishedException.class
    })
    public ProblemDetail handleBadRequest(RuntimeException ex) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() == null
                                ? "Некорректное значение"
                                : error.getDefaultMessage(),
                        (first, second) -> first
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации входных данных"
        );

        problem.setProperty("errors", errors);

        return problem;
    }
}

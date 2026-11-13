
package com.example.lmslite.controller;

import com.example.lmslite.dto.*;
import com.example.lmslite.service.EnrollmentService;
import com.example.lmslite.service.LessonService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final EnrollmentService enrollmentService;

    @PostMapping("/courses/{courseId}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    public LessonResponse createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateLessonRequest request
    ) {
        return lessonService.createLesson(courseId, request);
    }

    @GetMapping("/courses/{courseId}/lessons")
    public List<LessonResponse> getCourseLessons(
            @PathVariable Long courseId
    ) {
        return lessonService.getCourseLessons(courseId);
    }

    @GetMapping("/lessons/{id}")
    public LessonDetailsResponse getLessonDetails(
            @PathVariable Long id
    ) {
        return lessonService.getLessonDetails(id);
    }

    @PutMapping("/lessons/{id}")
    public LessonResponse updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLessonRequest request
    ) {
        return lessonService.updateLesson(id, request);
    }

    @DeleteMapping("/lessons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
    }

    @GetMapping("/lessons/{quizId}/questions")
    public List<QuestionResponse> getQuizQuestions(
            @PathVariable Long quizId
    ) {
        return lessonService.getQuizQuestions(quizId);
    }

    @PostMapping("/lessons/{quizId}/submit")
    public QuizResultResponse submitQuiz(
            @PathVariable Long quizId,
            @RequestParam Long enrollmentId,
            @Valid @RequestBody SubmitQuizRequest request
    ) {
        return enrollmentService.submitQuiz(
                enrollmentId,
                quizId,
                request
        );
    }
}

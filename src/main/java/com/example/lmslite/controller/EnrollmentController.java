package com.example.lmslite.controller;

import com.example.lmslite.dto.CreateEnrollmentRequest;
import com.example.lmslite.dto.EnrollmentResponse;
import com.example.lmslite.dto.ProgressResponse;
import com.example.lmslite.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse enrollStudent(
            @Valid @RequestBody CreateEnrollmentRequest request
    ) {
        return enrollmentService.enrollStudentResponse(
                request.studentId(),
                request.courseId()
        );
    }

    @GetMapping("/{id}")
    public ProgressResponse getEnrollmentDetails(
            @PathVariable Long id
    ) {
        return enrollmentService.getEnrollmentDetails(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dropEnrollment(@PathVariable Long id) {

        enrollmentService.dropEnrollment(id);
    }

    @PostMapping("/{id}/lessons/{lessonId}/complete")
    public ProgressResponse completeLesson(
            @PathVariable Long id,
            @PathVariable Long lessonId
    ) {
        return enrollmentService.completeLessonAndGetProgress(
                id,
                lessonId
        );
    }

    @GetMapping("/{id}/progress")
    public ProgressResponse getProgress(
            @PathVariable Long id
    ) {
        return enrollmentService.getEnrollmentDetails(id);
    }
}
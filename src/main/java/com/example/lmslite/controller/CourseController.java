
package com.example.lmslite.controller;

import com.example.lmslite.dto.CreateCourseRequest;
import com.example.lmslite.dto.CourseResponse;
import com.example.lmslite.dto.UpdateCourseRequest;
import com.example.lmslite.model.Course;
import com.example.lmslite.service.CourseService;
import com.example.lmslite.dto.CourseDetailsResponse;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(
            @Valid @RequestBody CreateCourseRequest request
    ) {

        Course course = courseService.createCourse(
                request.title(),
                request.description(),
                request.instructorId()
        );

        String authorName =
                course.getAuthor().getFirstName() + " " +
                        course.getAuthor().getLastName();

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                authorName,
                course.getStatus(),
                course.getTotalLessons(),
                course.getTotalEstimatedMinutes()
        );
    }

    @GetMapping
    public List<CourseResponse> getPublishedCourses() {
        return courseService.getPublishedCourseResponses();
    }

    @GetMapping("/{id}")
    public CourseDetailsResponse getCourseDetails(
            @PathVariable Long id
    ) {
        return courseService.getCourseDetails(id);
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        return courseService.updateCourse(
                id,
                request.title(),
                request.description(),
                request.instructorId()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    @PatchMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publishCourse(@PathVariable Long id) {
        courseService.publishCourse(id);
    }

    @PatchMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveCourse(@PathVariable Long id) {

        courseService.archiveCourse(id);
    }
}

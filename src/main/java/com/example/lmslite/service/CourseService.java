
package com.example.lmslite.service;

import com.example.lmslite.enums.CourseStatus;
import com.example.lmslite.exception.CourseNotFoundException;
import com.example.lmslite.exception.InstructorNotFoundException;
import com.example.lmslite.model.Course;
import com.example.lmslite.model.Instructor;
import com.example.lmslite.model.Lesson;
import com.example.lmslite.dto.CourseDetailsResponse;
import com.example.lmslite.dto.LessonResponse;

import java.util.Comparator;
import com.example.lmslite.repository.CourseRepository;
import com.example.lmslite.dto.CourseResponse;
import com.example.lmslite.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    @Transactional
    public Course createCourse(String title, String description, Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Инструктор не найден")
                );

        Course course = new Course();

        course.setTitle(title);
        course.setDescription(description);
        course.setAuthor(instructor);

        return courseRepository.save(course);
    }

    @Transactional
    public void publishCourse(Long courseId) {

        Course course = getCourseById(courseId);

        if (course.getTotalLessons() == 0) {
            throw new IllegalStateException(
                    "Нельзя опубликовать курс без уроков"
            );
        }

        course.publish();
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatus(
                CourseStatus.PUBLISHED
        );
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException("Курс не найден")
                );
    }


    @Transactional(readOnly = true)
    public List<CourseResponse> getPublishedCourseResponses() {

        List<Course> courses = getPublishedCourses();

        return courses.stream()
                .map(course -> {

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
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public CourseDetailsResponse getCourseDetails(Long courseId) {

        Course course = getCourseById(courseId);

        List<LessonResponse> lessons = course.getLessons()
                .stream()
                .sorted(Comparator.comparingInt(Lesson::getOrderIndex))
                .map(lesson -> new LessonResponse(
                        lesson.getId(),
                        lesson.getTitle(),
                        lesson.getType(),
                        lesson.getOrderIndex()
                ))
                .toList();

        String authorName =
                course.getAuthor().getFirstName() + " " +
                        course.getAuthor().getLastName();

        return new CourseDetailsResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                authorName,
                course.getStatus(),
                lessons.size(),
                course.getTotalEstimatedMinutes(),
                lessons
        );
    }

    @Transactional
    public CourseResponse updateCourse(
            Long courseId,
            String title,
            String description,
            Long instructorId
    ) {

        Course course = getCourseById(courseId);


        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() ->
                        new InstructorNotFoundException(
                                "Инструктор не найден"
                        )
                );

        course.setTitle(title);
        course.setDescription(description);
        course.setAuthor(instructor);

        String authorName =
                instructor.getFirstName() + " " + instructor.getLastName();

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

    @Transactional
    public void deleteCourse(Long courseId) {

        Course course = getCourseById(courseId);

        if (course.getStatus() != CourseStatus.DRAFT) {
            throw new IllegalStateException(
                    "Можно удалить только курс со статусом DRAFT"
            );
        }

        courseRepository.delete(course);
    }

    @Transactional
    public void archiveCourse(Long courseId) {

        Course course = getCourseById(courseId);

        course.archive();
    }
}

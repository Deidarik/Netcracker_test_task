package com.example.lmslite.service;

import com.example.lmslite.model.Instructor;
import com.example.lmslite.repository.InstructorRepository;
import com.example.lmslite.dto.CourseResponse;
import com.example.lmslite.model.Course;
import com.example.lmslite.repository.CourseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.lmslite.exception.InstructorNotFoundException;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Instructor registerInstructor(
            String firstName,
            String lastName,
            String email,
            String bio
    ) {
        Instructor instructor = new Instructor();

        instructor.setFirstName(firstName);
        instructor.setLastName(lastName);
        instructor.setEmail(email);
        instructor.setBio(bio);

        return instructorRepository.save(instructor);
    }




    @Transactional(readOnly = true)
    public List<CourseResponse> getInstructorCourses(Long instructorId) {

        // 1. Проверяем, что преподаватель существует
        Instructor instructor = getInstructorById(instructorId);

        // 2. Получаем его курсы
        List<Course> courses =
                courseRepository.findByAuthorId(instructorId);

        // 3. Преобразуем Entity в DTO
        String authorName =
                instructor.getFirstName() + " " + instructor.getLastName();

        return courses.stream()
                .map(course -> new CourseResponse(
                        course.getId(),
                        course.getTitle(),
                        course.getDescription(),
                        authorName,
                        course.getStatus(),
                        course.getTotalLessons(),
                        course.getTotalEstimatedMinutes()
                ))
                .toList();
    }


    public Instructor getInstructorById(Long id) {

        return instructorRepository.findById(id)
                .orElseThrow(() ->
                        new InstructorNotFoundException(
                                "Инструктор с ID " + id + " не найден"
                        )
                );
    }

}
package com.example.lmslite.service;

import com.example.lmslite.model.Student;
import com.example.lmslite.repository.StudentRepository;
import com.example.lmslite.exception.StudentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional
    public Student registerStudent(
            String firstName,
            String lastName,
            String email
    ) {
        Student student = new Student();

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);

        return studentRepository.save(student);
    }


    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Студент с ID " + id + " не найден"
                        )
                );
    }
}
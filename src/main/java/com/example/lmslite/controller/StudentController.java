
package com.example.lmslite.controller;

import com.example.lmslite.dto.EnrollmentResponse;
import com.example.lmslite.dto.RegisterStudentRequest;
import com.example.lmslite.dto.StudentResponse;
import com.example.lmslite.model.Student;
import com.example.lmslite.service.EnrollmentService;
import com.example.lmslite.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.lmslite.dto.CertificateResponse;
import com.example.lmslite.service.CertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final CertificateService certificateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse registerStudent(
            @Valid @RequestBody RegisterStudentRequest request
    ) {
        Student student = studentService.registerStudent(
                request.firstName(),
                request.lastName(),
                request.email()
        );

        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(
            @PathVariable Long id
    ) {
        Student student = studentService.getStudentById(id);

        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail()
        );
    }

    @GetMapping("/{id}/enrollments")
    public List<EnrollmentResponse> getStudentEnrollments(
            @PathVariable Long id
    ) {
        return enrollmentService.getStudentEnrollmentResponses(id);
    }

    @GetMapping("/{id}/certificates")
    public List<CertificateResponse> getStudentCertificates(
            @PathVariable Long id
    ) {
        return certificateService.getStudentCertificates(id);
    }
}

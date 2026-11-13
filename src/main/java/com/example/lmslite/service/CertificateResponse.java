
package com.example.lmslite.service;

import com.example.lmslite.dto.CertificateResponse;
import com.example.lmslite.enums.EnrollmentStatus;
import com.example.lmslite.exception.StudentNotFoundException;
import com.example.lmslite.exception.CertificateNotFoundException;
import com.example.lmslite.model.Certificate;
import com.example.lmslite.model.Enrollment;
import com.example.lmslite.model.Student;
import com.example.lmslite.model.Course;
import com.example.lmslite.repository.CertificateRepository;
import com.example.lmslite.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void issueCertificate(Enrollment enrollment) {

        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Курс ещё не завершён"
            );
        }

        Student student = enrollment.getStudent();
        Course course = enrollment.getCourse();

        if (certificateRepository.existsByStudentIdAndCourseId(
                student.getId(),
                course.getId()
        )) {
            return;
        }

        Certificate certificate = new Certificate();

        certificate.setStudent(student);
        certificate.setCourse(course);
        certificate.setIssuedAt(LocalDate.now());
        certificate.setUniqueCode(UUID.randomUUID().toString());

        certificateRepository.save(certificate);
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> getStudentCertificates(
            Long studentId
    ) {

        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(
                    "Студент не найден"
            );
        }

        return certificateRepository
                .findByStudentIdOrderByIssuedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificateResponse verifyCertificate(String code) {

        Certificate certificate = certificateRepository
                .findByUniqueCode(code)
                .orElseThrow(() ->
                        new CertificateNotFoundException(
                                "Сертификат не найден"
                        )
                );

        return toResponse(certificate);
    }

    @Transactional(readOnly = true)
    public String generateCertificateText(Long id) {

        Certificate certificate = certificateRepository
                .findById(id)
                .orElseThrow(() ->
                        new CertificateNotFoundException(
                                "Сертификат не найден"
                        )
                );

        Student student = certificate.getStudent();
        Course course = certificate.getCourse();

        String studentName =
                student.getFirstName() + " " + student.getLastName();

        String date = certificate.getIssuedAt().format(
                DateTimeFormatter.ofPattern(
                        "d MMMM uuuu",
                        Locale.forLanguageTag("ru-RU")
                )
        );

        return """
                ═══════════════════════════════════════════
                          СЕРТИФИКАТ О ПРОХОЖДЕНИИ
                ═══════════════════════════════════════════

                Настоящим подтверждается, что

                %s

                успешно завершил(а) курс

                "%s"

                Дата выдачи: %s
                Код сертификата: %s

                ═══════════════════════════════════════════
                """.formatted(
                studentName,
                course.getTitle(),
                date,
                certificate.getUniqueCode()
        );
    }

    private CertificateResponse toResponse(Certificate certificate) {

        Student student = certificate.getStudent();
        Course course = certificate.getCourse();

        return new CertificateResponse(
                certificate.getId(),
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                course.getId(),
                course.getTitle(),
                certificate.getIssuedAt(),
                certificate.getUniqueCode()
        );
    }
}

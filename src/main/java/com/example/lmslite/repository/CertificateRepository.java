package com.example.lmslite.repository;

import com.example.lmslite.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    List<Certificate> findByStudentIdOrderByIssuedAtDesc(
            Long studentId
    );

    Optional<Certificate> findByUniqueCode(String uniqueCode);
}
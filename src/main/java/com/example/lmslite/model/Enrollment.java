
package com.example.lmslite.model;

import com.example.lmslite.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @OneToMany(mappedBy = "enrollment")
    private Set<LessonProgress> completedLessons = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime enrolledAt;
    @Column(nullable = true)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }

    public int getProgressPercent() {
        int totalLessons = course.getTotalLessons();

        if (totalLessons == 0) {
            return 0;
        }

        long completedCount = completedLessons.stream()
                .filter(progress -> progress.getCompletedAt() != null)
                .count();

        return (int) (completedCount * 100L / totalLessons);
    }
}

package com.example.lmslite.model;


import com.example.lmslite.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static java.time.LocalDateTime.now;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name= "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Instructor author;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private CourseStatus status = CourseStatus.DRAFT;

    @OneToMany(mappedBy = "course")
    private List<Lesson> lessons = new ArrayList<>();

    private LocalDateTime createdAt;

    @Column(nullable = true)
    @Setter(AccessLevel.NONE)
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public int getTotalLessons(){
        return lessons.size();
    }

    public boolean isAvailableForEnrollment(){
        return status == CourseStatus.PUBLISHED;
    }

    public void publish() {
        if (status == CourseStatus.PUBLISHED) {
            return;
        }

        if (status == CourseStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Архивированный курс нельзя опубликовать"
            );
        }

        status = CourseStatus.PUBLISHED;
        publishedAt = LocalDateTime.now();
    }

    public void archive() {
        if (status == CourseStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Архивированный курс нельзя архивировать"
            );
        }

        status = CourseStatus.ARCHIVED;
    }

    public int getTotalEstimatedMinutes(){
        return lessons.stream().mapToInt(Lesson::getEstimatedMinutes).sum();
    }
}

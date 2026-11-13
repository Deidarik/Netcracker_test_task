package com.example.lmslite.model;

import com.example.lmslite.enums.LessonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class QuizLesson extends Lesson {

    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Question> questions = new ArrayList<>();

    @Min(0)
    @Max(100)
    @Column(precision = 5, scale = 2)
    private int passingScore;

    @Column(nullable = true)
    private Integer timeLimitMinutes;

    @Override
    public LessonType getType() {
        return LessonType.QUIZ;
    }

    @Override
    public int getEstimatedMinutes() {
        if (timeLimitMinutes != null) {
            return timeLimitMinutes;
        }

        return questions.size();
    }
}
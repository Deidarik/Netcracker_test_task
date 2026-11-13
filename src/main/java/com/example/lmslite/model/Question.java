
package com.example.lmslite.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @ElementCollection
    @CollectionTable(
            name = "question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @OrderColumn(name = "option_index")
    @Column(name = "option_text", nullable = false)
    private List<String> options = new ArrayList<>();

    private int correctOptionIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private QuizLesson quiz;

    public boolean isCorrectAnswer(int index) {
        return index == correctOptionIndex;
    }
}

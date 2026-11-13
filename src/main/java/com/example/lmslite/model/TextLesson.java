package com.example.lmslite.model;

import com.example.lmslite.enums.LessonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TextLesson extends Lesson {

    @Column(columnDefinition = "TEXT")
    private String content;

    private int readingTimeMinutes;

    @Override
    public LessonType getType() {
        return LessonType.TEXT;
    }

    @Override
    public int getEstimatedMinutes() {
        return readingTimeMinutes;
    }
}
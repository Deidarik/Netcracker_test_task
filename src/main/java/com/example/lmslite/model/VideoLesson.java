package com.example.lmslite.model;


import com.example.lmslite.enums.LessonType;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VideoLesson extends Lesson {

    private String videoUrl;
    private int durationMinutes;

    @Override
    public LessonType getType() {
        return LessonType.VIDEO;
    }

    @Override
    public int getEstimatedMinutes() {
        return durationMinutes;
    }
}

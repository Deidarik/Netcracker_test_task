
package com.example.lmslite.service;

import com.example.lmslite.dto.CreateLessonRequest;
import com.example.lmslite.dto.LessonDetailsResponse;
import com.example.lmslite.dto.LessonResponse;
import com.example.lmslite.dto.QuestionResponse;
import com.example.lmslite.exception.CourseNotFoundException;
import com.example.lmslite.exception.LessonNotFoundException;
import com.example.lmslite.model.*;
import com.example.lmslite.repository.*;
import com.example.lmslite.dto.UpdateLessonRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public LessonResponse createLesson(
            Long courseId,
            CreateLessonRequest request
    ) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new CourseNotFoundException("Курс не найден")
                );

        Lesson lesson = switch (request.type()) {

            case VIDEO -> {
                VideoLesson video = new VideoLesson();
                video.setVideoUrl(request.videoUrl());
                yield video;
            }

            case TEXT -> {
                TextLesson text = new TextLesson();
                text.setContent(request.content());
                yield text;
            }

            case QUIZ -> {
                QuizLesson quiz = new QuizLesson();
                quiz.setPassingScore(request.passingScore());
                yield quiz;
            }
        };


        lesson.setTitle(request.title());
        lesson.setOrderIndex(request.orderIndex());
        lesson.setCourse(course);


        Lesson savedLesson = lessonRepository.save(lesson);


        course.getLessons().add(savedLesson);

        if (savedLesson instanceof QuizLesson quiz) {

            for (CreateLessonRequest.QuestionRequest questionRequest
                    : request.questions()) {

                Question question = new Question();

                question.setText(questionRequest.text());
                question.setOptions(
                        new ArrayList<>(questionRequest.options())
                );
                question.setCorrectOptionIndex(
                        questionRequest.correctOptionIndex()
                );

                question.setQuiz(quiz);

                questionRepository.save(question);

                quiz.getQuestions().add(question);
            }
        }

        return new LessonResponse(
                savedLesson.getId(),
                savedLesson.getTitle(),
                request.type(),
                savedLesson.getOrderIndex()
        );
    }


    @Transactional(readOnly = true)
    public List<LessonResponse> getCourseLessons(Long courseId) {

        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(
                    "Курс с ID " + courseId + " не найден"
            );
        }

        List<Lesson> lessons =
                lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

        return lessons.stream()
                .map(lesson -> new LessonResponse(
                        lesson.getId(),
                        lesson.getTitle(),
                        lesson.getType(),
                        lesson.getOrderIndex()
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public LessonDetailsResponse getLessonDetails(Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() ->
                        new LessonNotFoundException(
                                "Урок с ID " + lessonId + " не найден"
                        )
                );

        String videoUrl = null;
        String content = null;
        Integer passingScore = null;

        List<QuestionResponse> questions = null;

        if (lesson instanceof VideoLesson video) {

            videoUrl = video.getVideoUrl();

        } else if (lesson instanceof TextLesson text) {

            content = text.getContent();

        } else if (lesson instanceof QuizLesson quiz) {

            passingScore = quiz.getPassingScore();

            questions = quiz.getQuestions()
                    .stream()
                    .map(question ->
                            new QuestionResponse(
                                    question.getId(),
                                    question.getText(),
                                    new ArrayList<>(question.getOptions())
                            )
                    )
                    .toList();
        }


        return new LessonDetailsResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getType(),
                lesson.getOrderIndex(),
                videoUrl,
                content,
                passingScore,
                questions
        );
    }



    @Transactional
    public LessonResponse updateLesson(Long lessonId, UpdateLessonRequest request) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() ->
                        new LessonNotFoundException("Урок не найден")
                );

        if (lesson instanceof VideoLesson video) {

            if (request.videoUrl() == null
                    || request.videoUrl().isBlank()) {
                throw new IllegalArgumentException(
                        "Для видеоурока необходимо указать videoUrl"
                );
            }

            video.setVideoUrl(request.videoUrl());

        } else if (lesson instanceof TextLesson text) {

            if (request.content() == null
                    || request.content().isBlank()) {
                throw new IllegalArgumentException(
                        "Для текстового урока необходимо указать content"
                );
            }

            text.setContent(request.content());

        } else if (lesson instanceof QuizLesson quiz) {

            updateQuiz(quiz, request);
        }

        lesson.setTitle(request.title());
        lesson.setOrderIndex(request.orderIndex());

        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getType(),
                lesson.getOrderIndex()
        );
    }




    private void updateQuiz(
            QuizLesson quiz,
            UpdateLessonRequest request
    ) {

        if (request.passingScore() == null) {
            throw new IllegalArgumentException(
                    "Для теста необходимо указать passingScore"
            );
        }

        if (request.questions() == null
                || request.questions().isEmpty()) {
            throw new IllegalArgumentException(
                    "Тест должен содержать хотя бы один вопрос"
            );
        }

        quiz.setPassingScore(request.passingScore());

        quiz.getQuestions().clear();

        for (UpdateLessonRequest.QuestionRequest questionRequest
                : request.questions()) {

            Question question = new Question();

            question.setText(questionRequest.text());

            question.setOptions(
                    new ArrayList<>(questionRequest.options())
            );

            question.setCorrectOptionIndex(
                    questionRequest.correctOptionIndex()
            );

            question.setQuiz(quiz);

            quiz.getQuestions().add(question);
        }
    }



    @Transactional
    public void deleteLesson(Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() ->
                        new LessonNotFoundException(
                                "Урок с ID " + lessonId + " не найден"
                        )
                );

        if (lessonProgressRepository.existsByLessonId(lessonId)) {
            throw new IllegalStateException(
                    "Нельзя удалить урок, по которому есть прогресс студентов"
            );
        }

        lessonRepository.delete(lesson);
    }


    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuizQuestions(Long quizId) {

        Lesson lesson = lessonRepository.findById(quizId)
                .orElseThrow(() ->
                        new LessonNotFoundException(
                                "Урок с ID " + quizId + " не найден"
                        )
                );

        if (!(lesson instanceof QuizLesson quiz)) {
            throw new IllegalArgumentException(
                    "Урок с ID " + quizId + " не является тестом"
            );
        }

        return quiz.getQuestions()
                .stream()
                .map(question -> new QuestionResponse(
                        question.getId(),
                        question.getText(),
                        new ArrayList<>(question.getOptions())
                ))
                .toList();
    }

}


package com.example.lmslite.service;

import com.example.lmslite.dto.EnrollmentResponse;
import com.example.lmslite.enums.EnrollmentStatus;
import com.example.lmslite.exception.AlreadyEnrolledException;
import com.example.lmslite.exception.CourseNotFoundException;
import com.example.lmslite.exception.CourseNotPublishedException;
import com.example.lmslite.exception.StudentNotFoundException;
import com.example.lmslite.model.*;
import com.example.lmslite.repository.*;
import com.example.lmslite.dto.ProgressResponse;
import com.example.lmslite.dto.SubmitQuizRequest;
import com.example.lmslite.dto.QuizResultResponse;
import com.example.lmslite.exception.LessonNotFoundException;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CertificateService certificateService;

    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new StudentNotFoundException("Студент не найден")
                );

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new CourseNotFoundException("Курс не найден")
                );

        if (!course.isAvailableForEnrollment()) {
            throw new CourseNotPublishedException(
                    "Курс не опубликован"
            );
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(
                studentId, courseId)) {
            throw new AlreadyEnrolledException(
                    "Студент уже записан на курс"
            );
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        return enrollmentRepository.save(enrollment);
    }

    private LessonProgress getOrCreateLessonProgress(
            Enrollment enrollment,
            Lesson lesson
    ) {
        return lessonProgressRepository
                .findByEnrollmentIdAndLessonId(
                        enrollment.getId(),
                        lesson.getId()
                )
                .orElseGet(() -> {
                    LessonProgress progress = new LessonProgress();
                    progress.setEnrollment(enrollment);
                    progress.setLesson(lesson);
                    return progress;
                });
    }

    @Transactional(readOnly = true)
    public List<Enrollment> getStudentEnrollments(Long studentId) {

        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException("Студент не найден");
        }

        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional
    public int completeLesson(Long enrollmentId, Long lessonId) {

        Enrollment enrollment = getEnrollmentById(enrollmentId);

        boolean alreadyCompleted = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .map(progress -> progress.getCompletedAt() != null)
                .orElse(false);

        if (alreadyCompleted) {
            return enrollment.getProgressPercent();
        }

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Запись на курс неактивна"
            );
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Урок не найден")
                );


        if (!lesson.getCourse().getId()
                .equals(enrollment.getCourse().getId())) {
            throw new IllegalArgumentException(
                    "Урок не принадлежит данному курсу"
            );
        }

        if (lesson instanceof QuizLesson) {
            throw new IllegalStateException(
                    "Тест необходимо пройти через submitQuiz"
            );
        }

        return markLessonCompleted(enrollment, lesson);
    }

    @Transactional(readOnly = true)
    public int getProgress(Long enrollmentId) {

        Enrollment enrollment = getEnrollmentById(enrollmentId);

        return enrollment.getProgressPercent();
    }


    private int markLessonCompleted(
            Enrollment enrollment,
            Lesson lesson
    ) {
        LessonProgress progress =
                getOrCreateLessonProgress(enrollment, lesson);

        return markLessonCompleted(enrollment, progress);
    }

    private int markLessonCompleted(
            Enrollment enrollment,
            LessonProgress progress
    ) {
        if (progress.getCompletedAt() != null) {
            return enrollment.getProgressPercent();
        }


        progress.setCompletedAt(LocalDateTime.now());

        progress = lessonProgressRepository.save(progress);

        enrollment.getCompletedLessons().add(progress);

        int percent = enrollment.getProgressPercent();


        if (enrollment.getCourse().getTotalLessons() > 0
                && percent == 100) {

            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        certificateService.issueCertificate(enrollment);

        return percent;
    }


    private Enrollment getEnrollmentById(Long enrollmentId) {

        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Запись на курс не найдена"
                        )
                );
    }

    @Transactional
    public EnrollmentResponse enrollStudentResponse(
            Long studentId,
            Long courseId
    ) {

        Enrollment enrollment = enrollStudent(studentId, courseId);

        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getCourse().getId(),
                enrollment.getStatus(),
                enrollment.getEnrolledAt()
        );
    }


    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getStudentEnrollmentResponses(
            Long studentId
    ) {

        List<Enrollment> enrollments =
                getStudentEnrollments(studentId);


        return enrollments.stream()
                .map(enrollment -> new EnrollmentResponse(
                        enrollment.getId(),
                        enrollment.getStudent().getId(),
                        enrollment.getCourse().getId(),
                        enrollment.getStatus(),
                        enrollment.getEnrolledAt()
                ))
                .toList();
    }


    @Transactional(readOnly = true)
    public ProgressResponse getEnrollmentDetails(Long enrollmentId) {

        Enrollment enrollment = getEnrollmentById(enrollmentId);

        Course course = enrollment.getCourse();

        List<Lesson> lessons =
                lessonRepository.findByCourseIdOrderByOrderIndexAsc(
                        course.getId()
                );

        Set<Long> completedLessonIds = enrollment.getCompletedLessons()
                .stream()
                .filter(progress -> progress.getCompletedAt() != null)
                .map(progress -> progress.getLesson().getId())
                .collect(Collectors.toSet());

        List<ProgressResponse.LessonItem> lessonItems =
                lessons.stream()
                        .map(lesson -> new ProgressResponse.LessonItem(
                                lesson.getId(),
                                lesson.getTitle(),
                                completedLessonIds.contains(lesson.getId())
                        ))
                        .toList();


        int completedCount = (int) lessonItems.stream()
                .filter(ProgressResponse.LessonItem::completed)
                .count();

        return new ProgressResponse(
                enrollment.getId(),
                course.getTitle(),
                enrollment.getProgressPercent(),
                completedCount,
                lessonItems.size(),
                enrollment.getStatus(),
                lessonItems
        );
    }

    @Transactional
    public void dropEnrollment(Long enrollmentId) {

        Enrollment enrollment = getEnrollmentById(enrollmentId);

        enrollment.setStatus(EnrollmentStatus.DROPPED);
    }

    @Transactional
    public ProgressResponse completeLessonAndGetProgress(
            Long enrollmentId,
            Long lessonId
    ) {
        completeLesson(enrollmentId, lessonId);

        return getEnrollmentDetails(enrollmentId);
    }



    @Transactional
    public QuizResultResponse submitQuiz(
            Long enrollmentId,
            Long quizId,
            SubmitQuizRequest request
    ) {

        Enrollment enrollment = getEnrollmentById(enrollmentId);

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Запись на курс неактивна"
            );
        }

        Lesson lesson = lessonRepository.findById(quizId)
                .orElseThrow(() ->
                        new LessonNotFoundException("Тест не найден")
                );

        if (!(lesson instanceof QuizLesson quiz)) {
            throw new IllegalArgumentException(
                    "Указанный урок не является тестом"
            );
        }

        if (!quiz.getCourse().getId()
                .equals(enrollment.getCourse().getId())) {
            throw new IllegalArgumentException(
                    "Тест не принадлежит данному курсу"
            );
        }

        // 4. Получаем существующий или новый LessonProgress
        LessonProgress progress =
                getOrCreateLessonProgress(enrollment, quiz);

        if (progress.getCompletedAt() != null) {
            throw new IllegalStateException(
                    "Тест уже успешно сдан"
            );
        }

        List<Question> questions = quiz.getQuestions();

        int correctCount = countCorrectAnswers(
                questions, request
        );

        int totalQuestions = questions.size();

        int scorePercent = correctCount * 100 / totalQuestions;

        boolean passed = scorePercent >= quiz.getPassingScore();

        progress.setQuizScore(scorePercent);

        if (passed) {
            markLessonCompleted(enrollment, progress);

        } else {
            lessonProgressRepository.save(progress);
        }

        return new QuizResultResponse(
                quiz.getId(),
                quiz.getTitle(),
                totalQuestions,
                correctCount,
                scorePercent,
                passed,
                quiz.getPassingScore()
        );
    }



    private int countCorrectAnswers(
            List<Question> questions,
            SubmitQuizRequest request
    ) {

        if (questions.isEmpty()) {
            throw new IllegalStateException(
                    "В тесте отсутствуют вопросы"
            );
        }

        Map<Long, Integer> selectedAnswers = new HashMap<>();

        for (SubmitQuizRequest.Answer answer : request.answers()) {

            if (selectedAnswers.putIfAbsent(
                    answer.questionId(),
                    answer.selectedOption()
            ) != null) {
                throw new IllegalArgumentException(
                        "Один вопрос указан несколько раз"
                );
            }
        }

        if (selectedAnswers.size() != questions.size()) {
            throw new IllegalArgumentException(
                    "Нужно ответить на все вопросы теста"
            );
        }

        int correctCount = 0;

        for (Question question : questions) {

            Integer selectedOption =
                    selectedAnswers.get(question.getId());

            if (selectedOption == null) {
                throw new IllegalArgumentException(
                        "Передан неверный ID вопроса"
                );
            }

            if (selectedOption < 0
                    || selectedOption >= question.getOptions().size()) {
                throw new IllegalArgumentException(
                        "Указан несуществующий вариант ответа"
                );
            }

            if (selectedOption == question.getCorrectOptionIndex()) {
                correctCount++;
            }
        }

        return correctCount;
    }
}

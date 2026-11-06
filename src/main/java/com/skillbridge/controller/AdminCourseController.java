package com.skillbridge.controller;

import com.skillbridge.dto.CourseCreateUpdateRequest;
import com.skillbridge.dto.LessonCreateUpdateRequest;
import com.skillbridge.dto.QuizQuestionCreateRequest;
import com.skillbridge.model.Course;
import com.skillbridge.model.Lesson;
import com.skillbridge.model.LessonQuizQuestion;
import com.skillbridge.repository.CourseRepository;
import com.skillbridge.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {

    private final CourseService courseService;
    private final CourseRepository courseRepository;

    public AdminCourseController(CourseService courseService, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@Valid @RequestBody CourseCreateUpdateRequest request) {
        return courseService.createCourse(request);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @Valid @RequestBody CourseCreateUpdateRequest request) {
        return courseService.updateCourse(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    @PatchMapping("/{id}/publish")
    public Course togglePublish(@PathVariable Long id) {
        return courseService.togglePublish(id);
    }

    @PostMapping("/{courseId}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    public Lesson addLesson(@PathVariable Long courseId, @Valid @RequestBody LessonCreateUpdateRequest request) {
        return courseService.createLesson(courseId, request);
    }

    @PutMapping("/lessons/{lessonId}")
    public Lesson updateLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonCreateUpdateRequest request) {
        return courseService.updateLesson(lessonId, request);
    }

    @DeleteMapping("/lessons/{lessonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable Long lessonId) {
        courseService.deleteLesson(lessonId);
    }

    @PostMapping("/lessons/{lessonId}/quiz")
    public List<LessonQuizQuestion> setLessonQuiz(
            @PathVariable Long lessonId,
            @RequestBody List<QuizQuestionCreateRequest> questions
    ) {
        return courseService.setLessonQuizQuestions(lessonId, questions);
    }
}

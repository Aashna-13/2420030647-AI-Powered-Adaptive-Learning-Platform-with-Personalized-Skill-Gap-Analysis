package com.skillbridge.controller;

import com.skillbridge.dto.*;
import com.skillbridge.model.QuestionDifficulty;
import com.skillbridge.model.User;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseCatalogController {

    private final CourseService courseService;
    private final UserRepository userRepository;

    public CourseCatalogController(CourseService courseService, UserRepository userRepository) {
        this.courseService = courseService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<CourseDto> getCourses(
            @RequestParam(required = false) String careerId,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) QuestionDifficulty difficulty,
            @RequestParam(required = false) String query,
            Authentication authentication
    ) {
        User user = resolveUser(authentication);
        return courseService.getCourses(user, careerId, skill, difficulty, query);
    }

    @GetMapping("/my-enrollments")
    public List<CourseDto> getMyEnrollments(Authentication authentication) {
        User user = requireUser(authentication);
        return courseService.getStudentEnrollments(user);
    }

    @GetMapping("/recommendations")
    public List<CourseRecommendationDto> getRecommendations(Authentication authentication) {
        User user = resolveUser(authentication);
        return courseService.getPersonalizedRecommendations(user);
    }

    @GetMapping("/roadmap")
    public LearningRoadmapDto getRoadmap(Authentication authentication) {
        User user = requireUser(authentication);
        return courseService.getLearningRoadmap(user);
    }

    @GetMapping("/{idOrSlug}")
    public Map<String, Object> getCourseDetails(
            @PathVariable String idOrSlug,
            Authentication authentication
    ) {
        User user = resolveUser(authentication);
        return courseService.getCourseDetails(idOrSlug, user);
    }

    @PostMapping("/{idOrSlug}/enroll")
    public CourseDto enroll(
            @PathVariable String idOrSlug,
            Authentication authentication
    ) {
        User user = requireUser(authentication);
        return courseService.enrollStudent(idOrSlug, user);
    }

    @GetMapping("/{courseId}/lessons/{lessonId}")
    public LessonDetailDto getLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            Authentication authentication
    ) {
        User user = resolveUser(authentication);
        return courseService.getLesson(lessonId, user);
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public Map<String, Object> markComplete(
            @PathVariable Long lessonId,
            Authentication authentication
    ) {
        User user = requireUser(authentication);
        return courseService.markLessonComplete(lessonId, user);
    }

    @GetMapping("/lessons/{lessonId}/quiz")
    public List<QuizQuestionDto> getLessonQuiz(
            @PathVariable Long lessonId
    ) {
        return courseService.getLessonQuiz(lessonId);
    }

    @PostMapping("/lessons/{lessonId}/quiz")
    public QuizSubmissionResultDto submitLessonQuiz(
            @PathVariable Long lessonId,
            @RequestBody QuizSubmissionRequest request,
            Authentication authentication
    ) {
        User user = requireUser(authentication);
        return courseService.submitLessonQuiz(lessonId, user, request);
    }

    private User resolveUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            return userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        }
        return null;
    }

    private User requireUser(Authentication authentication) {
        User user = resolveUser(authentication);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in is required for this action.");
        }
        return user;
    }
}

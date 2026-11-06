package com.skillbridge.controller;

import com.skillbridge.dto.CareerAssessmentResultDto;
import com.skillbridge.dto.CareerAssessmentSubmissionRequest;
import com.skillbridge.dto.CareerDto;
import com.skillbridge.dto.CareerQuestionDto;
import com.skillbridge.model.User;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.service.CareerAssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
public class CareerAssessmentController {

    private final CareerAssessmentService careerAssessmentService;
    private final UserRepository userRepository;
    private final com.skillbridge.repository.CareerAssessmentRepository assessmentRepository;

    public CareerAssessmentController(
            CareerAssessmentService careerAssessmentService,
            UserRepository userRepository,
            com.skillbridge.repository.CareerAssessmentRepository assessmentRepository
    ) {
        this.careerAssessmentService = careerAssessmentService;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
    }

    @GetMapping
    public List<CareerDto> getCareers() {
        return careerAssessmentService.getAllCareers();
    }

    @GetMapping("/{careerId}")
    public CareerDto getCareer(@PathVariable String careerId) {
        return careerAssessmentService.getCareerById(careerId);
    }

    @GetMapping("/{careerId}/questions")
    public List<CareerQuestionDto> getCareerQuestions(@PathVariable String careerId) {
        return careerAssessmentService.getQuestionsForCareer(careerId);
    }

    @GetMapping("/{careerId}/adaptive-questions")
    public List<CareerQuestionDto> getAdaptiveQuestions(@PathVariable String careerId, Authentication authentication) {
        User currentUser = resolveUser(authentication);
        return careerAssessmentService.getAdaptiveQuestions(careerId, currentUser,
                assessmentRepository);
    }

    @PostMapping("/{careerId}/assessments")
    @ResponseStatus(HttpStatus.CREATED)
    public CareerAssessmentResultDto submitAssessment(
            @PathVariable String careerId,
            @Valid @RequestBody CareerAssessmentSubmissionRequest request,
            Authentication authentication
    ) {
        User currentUser = resolveUser(authentication);
        return careerAssessmentService.submitAssessment(careerId, currentUser, request);
    }

    @GetMapping("/assessments/my")
    public List<CareerAssessmentResultDto> getMyAssessments(Authentication authentication) {
        User currentUser = resolveUser(authentication);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to view assessment history");
        }
        return careerAssessmentService.getAssessmentsForUser(currentUser);
    }

    @GetMapping("/assessments/{id}")
    public CareerAssessmentResultDto getAssessmentDetails(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User currentUser = resolveUser(authentication);
        return careerAssessmentService.getAssessmentById(id, currentUser);
    }

    private User resolveUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            return userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        }
        return null;
    }
}

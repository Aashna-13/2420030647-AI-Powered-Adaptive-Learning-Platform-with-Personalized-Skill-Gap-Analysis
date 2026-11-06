package com.skillbridge.controller;

import com.skillbridge.model.Assessment;
import com.skillbridge.model.Topic;
import com.skillbridge.model.User;
import com.skillbridge.repository.AssessmentRepository;
import com.skillbridge.repository.TopicRepository;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.service.SkillGapService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final TopicRepository topics;
    private final AssessmentRepository assessments;
    private final UserRepository userRepository;
    private final SkillGapService skillGapService;

    public ApiController(
            TopicRepository topics,
            AssessmentRepository assessments,
            UserRepository userRepository,
            SkillGapService skillGapService
    ) {
        this.topics = topics;
        this.assessments = assessments;
        this.userRepository = userRepository;
        this.skillGapService = skillGapService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "SkillBridge AI");
    }

    @GetMapping("/topics")
    public List<Topic> getTopics() {
        return topics.findAll();
    }

    @PostMapping("/assessments")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createAssessment(
            @Valid @RequestBody AssessmentRequest request,
            Authentication authentication
    ) {
        Topic topic = topics.findById(request.topicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));

        User loggedInUser = null;
        String learnerName = request.learnerName() != null ? request.learnerName().trim() : "Demo Student";

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            loggedInUser = userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
            if (loggedInUser != null) {
                learnerName = loggedInUser.getFullName();
            }
        }

        Assessment assessment = new Assessment(
                learnerName,
                topic,
                request.score(),
                topic.getTargetScore(),
                loggedInUser
        );
        Assessment saved = assessments.save(assessment);
        return skillGapService.analyze(saved);
    }

    @GetMapping("/assessments")
    public List<Assessment> getAssessments(
            @RequestParam(required = false) String learner,
            Authentication authentication
    ) {
        // If authenticated as student, strictly isolate records to their own account
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            Optional<User> currentUserOpt = userRepository.findByEmailIgnoreCase(authentication.getName());
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                // Admin can query any learner or view all
                if (currentUser.getRole().name().equals("ADMIN")) {
                    if (learner != null && !learner.isBlank()) {
                        return assessments.findByLearnerNameIgnoreCaseOrderByCreatedAtDesc(learner.trim());
                    }
                    return assessments.findAllByOrderByCreatedAtDesc();
                }

                // Student only sees their own assessments
                return assessments.findByUserOrLearnerNameIgnoreCaseOrderByCreatedAtDesc(
                        currentUser,
                        currentUser.getFullName()
                );
            }
        }

        // Unauthenticated or fallback query
        String queryLearner = (learner != null && !learner.isBlank()) ? learner.trim() : "Demo Student";
        return assessments.findByLearnerNameIgnoreCaseOrderByCreatedAtDesc(queryLearner);
    }

    public record AssessmentRequest(
            @NotBlank(message = "Learner name cannot be blank")
            @Size(max = 80, message = "Learner name must not exceed 80 characters")
            String learnerName,

            @NotNull(message = "Topic id is required")
            Long topicId,

            @Min(value = 0, message = "Score must be at least 0")
            @Max(value = 100, message = "Score must be at most 100")
            int score
    ) {}
}



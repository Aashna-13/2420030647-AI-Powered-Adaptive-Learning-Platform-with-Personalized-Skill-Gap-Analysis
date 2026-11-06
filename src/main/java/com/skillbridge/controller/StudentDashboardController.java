package com.skillbridge.controller;

import com.skillbridge.dto.StudentDashboardResponse;
import com.skillbridge.dto.UserResponse;
import com.skillbridge.model.Assessment;
import com.skillbridge.model.User;
import com.skillbridge.repository.AssessmentRepository;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.service.SkillGapService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentDashboardController {

    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final SkillGapService skillGapService;

    public StudentDashboardController(
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            SkillGapService skillGapService
    ) {
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.skillGapService = skillGapService;
    }

    @GetMapping("/dashboard")
    public StudentDashboardResponse getStudentDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        String email = authentication.getName();
        User student = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found."));

        // Strictly isolate student data: query by User entity or learner name fallback
        List<Assessment> studentAssessments = assessmentRepository.findByUserOrLearnerNameIgnoreCaseOrderByCreatedAtDesc(
                student,
                student.getFullName()
        );

        int total = studentAssessments.size();
        double avgScore = 0.0;
        int avgGap = 0;
        String currentSkillLevel = "Not Assessed";
        String overallRecommendation = "Welcome to SkillBridge AI! Take your first assessment to discover your personalized skill gaps.";

        List<Map<String, Object>> historyList = new ArrayList<>();

        if (total > 0) {
            int scoreSum = 0;
            int gapSum = 0;

            for (Assessment a : studentAssessments) {
                Map<String, Object> analyzed = skillGapService.analyze(a);
                Map<String, Object> item = new LinkedHashMap<>(analyzed);
                item.put("createdAt", a.getCreatedAt());
                item.put("careerRole", a.getTopic() != null ? a.getTopic().getCareerRole() : "General");
                historyList.add(item);

                int sc = a.getScore();
                int tg = a.getTargetScore();
                scoreSum += sc;
                gapSum += Math.max(0, tg - sc);
            }

            avgScore = Math.round((double) scoreSum / total * 10.0) / 10.0;
            avgGap = Math.round((float) gapSum / total);
            currentSkillLevel = skillGapService.determineLevel((int) Math.round(avgScore));

            if (avgScore >= 80) {
                overallRecommendation = "Exceptional performance! Your average score of " + avgScore + "% exceeds benchmark targets. Consider tackling advanced challenges.";
            } else if (avgScore >= 60) {
                overallRecommendation = "Solid foundation! You are at an Intermediate level. Focus on closing the remaining " + avgGap + "-point gap across your topics.";
            } else {
                overallRecommendation = "Skill gap analysis shows growth opportunities. Follow the targeted study modules to build proficiency towards the 80% benchmark.";
            }
        }

        UserResponse studentDto = new UserResponse(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getRole().name(),
                student.getCreatedAt()
        );

        return new StudentDashboardResponse(
                studentDto,
                total,
                avgScore,
                currentSkillLevel,
                avgGap,
                overallRecommendation,
                historyList
        );
    }
}

package com.skillbridge.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.SkillScoreDto;
import com.skillbridge.model.CareerAssessment;
import com.skillbridge.model.User;
import com.skillbridge.repository.CareerAssessmentRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

/** Student-owned analytics and roadmap endpoints. All results are scoped to the authenticated user. */
@RestController
@RequestMapping("/api/student")
public class StudentInsightsController {
    private final UserRepository users;
    private final CareerAssessmentRepository assessments;
    private final ObjectMapper mapper;
    private final com.skillbridge.service.AiLearningService aiLearningService;

    public StudentInsightsController(UserRepository users, CareerAssessmentRepository assessments, ObjectMapper mapper,
                                     com.skillbridge.service.AiLearningService aiLearningService) {
        this.users = users;
        this.assessments = assessments;
        this.mapper = mapper;
        this.aiLearningService = aiLearningService;
    }

    @GetMapping("/insights")
    public Map<String, Object> insights(Authentication authentication) {
        User user = currentUser(authentication);
        List<CareerAssessment> records = assessments.findByUserOrderByCreatedAtDesc(user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("assessmentCount", records.size());
        result.put("latestCareer", records.isEmpty() ? "Not assessed" : records.get(0).getCareerTitle());
        result.put("latestReadiness", records.isEmpty() ? 0 : records.get(0).getReadinessPercentage());
        result.put("scoreTrend", records.stream().sorted(Comparator.comparing(CareerAssessment::getCreatedAt))
                .map(a -> Map.of("date", a.getCreatedAt().toLocalDate().toString(), "score", a.getReadinessPercentage(), "career", a.getCareerTitle()))
                .collect(Collectors.toList()));
        Map<String, List<Integer>> scoresBySkill = new LinkedHashMap<>();
        for (CareerAssessment record : records) {
            for (SkillScoreDto skill : parseSkills(record)) {
                scoresBySkill.computeIfAbsent(skill.skillName(), k -> new ArrayList<>()).add(skill.score());
            }
        }
        List<Map<String, Object>> skillSummary = new ArrayList<>();
        scoresBySkill.forEach((name, scores) -> {
            double avg = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
            skillSummary.add(Map.of("skill", name, "averageScore", Math.round(avg), "attempts", scores.size()));
        });
        skillSummary.sort(Comparator.comparingInt(m -> ((Number)m.get("averageScore")).intValue()));
        result.put("skillSummary", skillSummary);
        result.put("message", records.isEmpty() ? "Complete a career assessment to unlock your progress analytics." : "Analytics are based on your saved career assessments.");
        return result;
    }

    @GetMapping("/roadmap")
    public Map<String, Object> roadmap(Authentication authentication) {
        User user = currentUser(authentication);
        List<CareerAssessment> records = assessments.findByUserOrderByCreatedAtDesc(user);
        Map<String, Object> result = new LinkedHashMap<>();
        if (records.isEmpty()) {
            result.put("career", "Choose a career first");
            result.put("summary", "Take a career assessment to generate a learning plan from your actual skill gaps.");
            result.put("tasks", List.of());
            result.put("generatedBy", "SkillBridge rule-based recommendation engine");
            return result;
        }
        CareerAssessment latest = records.get(0);
        List<SkillScoreDto> skills = parseSkills(latest);
        List<Map<String, Object>> tasks = new ArrayList<>();
        skills.stream().filter(s -> s.gap() > 0).sorted(Comparator.comparingInt(SkillScoreDto::gap).reversed()).forEach(s -> {
            String level = s.score() < 40 ? "Beginner" : s.score() < 60 ? "Foundations" : "Practice";
            String action = s.score() < 40 ? "Review fundamentals and complete a guided beginner lesson" : s.score() < 60 ? "Study core concepts and solve 5–10 practice problems" : "Build a small applied exercise and review edge cases";
            String resourceUrl = resourceFor(s.skillName());
            tasks.add(Map.of("skill", s.skillName(), "currentScore", s.score(), "target", s.targetScore(), "gap", s.gap(), "level", level,
                    "action", action, "resourceTitle", "Recommended learning resource", "resourceUrl", resourceUrl,
                    "estimatedHours", s.score() < 40 ? 4 : s.score() < 60 ? 3 : 2, "status", "TODO"));
        });
        result.put("career", latest.getCareerTitle());
        result.put("basedOnAssessmentId", latest.getId());
        result.put("summary", tasks.isEmpty() ? "You met the target for every assessed skill. Try an advanced assessment or build a portfolio project." : "Start with the largest gaps, complete each learning action, then retake the assessment to measure progress.");
        result.put("tasks", tasks);
        String aiAdvice = aiLearningService.createStudyAdvice(latest.getCareerTitle(), tasks);
        result.put("aiAdvice", aiAdvice);
        result.put("generatedBy", aiAdvice == null
                ? "SkillBridge adaptive rules engine (configure GEMINI_API_KEY for AI-generated study advice)"
                : "Gemini AI study advice + SkillBridge adaptive gap rules");
        return result;
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        return users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found."));
    }

    private List<SkillScoreDto> parseSkills(CareerAssessment assessment) {
        try {
            if (assessment.getSkillBreakdownJson() == null || assessment.getSkillBreakdownJson().isBlank()) return List.of();
            return mapper.readValue(assessment.getSkillBreakdownJson(), new TypeReference<List<SkillScoreDto>>() {});
        } catch (Exception e) { return List.of(); }
    }

    private String resourceFor(String skill) {
        String s = skill.toLowerCase(Locale.ROOT);
        if (s.contains("java")) return "https://dev.java/learn/";
        if (s.contains("sql") || s.contains("database")) return "https://www.w3schools.com/sql/";
        if (s.contains("spring")) return "https://spring.io/guides";
        if (s.contains("html") || s.contains("css") || s.contains("javascript")) return "https://developer.mozilla.org/en-US/docs/Learn";
        if (s.contains("aws") || s.contains("cloud")) return "https://skillbuilder.aws/";
        if (s.contains("python")) return "https://docs.python.org/3/tutorial/";
        if (s.contains("security") || s.contains("cyber")) return "https://owasp.org/www-project-top-ten/";
        return "https://www.freecodecamp.org/learn/";
    }
}

package com.skillbridge.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.SkillScoreDto;
import com.skillbridge.model.CareerAssessment;
import com.skillbridge.model.Role;
import com.skillbridge.repository.CareerAssessmentRepository;
import com.skillbridge.repository.CareerRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCareerController {
    private final UserRepository users;
    private final CareerRepository careers;
    private final CareerAssessmentRepository assessments;
    private final ObjectMapper mapper;

    public AdminCareerController(UserRepository users, CareerRepository careers, CareerAssessmentRepository assessments, ObjectMapper mapper) {
        this.users = users; this.careers = careers; this.assessments = assessments; this.mapper = mapper;
    }

    @GetMapping("/career-overview")
    public Map<String, Object> overview() {
        List<CareerAssessment> all = assessments.findAllByOrderByCreatedAtDesc();
        double average = all.stream().mapToInt(CareerAssessment::getReadinessPercentage).average().orElse(0);
        Map<String, Long> careerCounts = new TreeMap<>();
        all.forEach(a -> careerCounts.merge(a.getCareerTitle(), 1L, Long::sum));
        long students = users.findAll().stream().filter(u -> u.getRole() == Role.STUDENT).count();
        return Map.of("studentCount", students, "careerCount", careers.count(), "careerAssessmentCount", all.size(),
                "averageReadiness", Math.round(average * 10.0) / 10.0, "careerDistribution", careerCounts);
    }

    @GetMapping("/career-assessments")
    public List<Map<String, Object>> careerAssessments() {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CareerAssessment a : assessments.findAllByOrderByCreatedAtDesc()) {
            rows.add(Map.of("id", a.getId(), "studentName", a.getStudentName(), "studentEmail", a.getStudentEmail(),
                    "career", a.getCareerTitle(), "score", a.getOverallScore(), "readiness", a.getReadinessPercentage(),
                    "correctAnswers", a.getCorrectAnswers(), "totalQuestions", a.getTotalQuestions(), "createdAt", a.getCreatedAt()));
        }
        return rows;
    }
}

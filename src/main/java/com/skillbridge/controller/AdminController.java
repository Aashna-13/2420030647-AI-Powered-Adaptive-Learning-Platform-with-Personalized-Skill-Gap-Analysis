package com.skillbridge.controller;

import com.skillbridge.dto.UserResponse;
import com.skillbridge.model.Assessment;
import com.skillbridge.model.Role;
import com.skillbridge.repository.AssessmentRepository;
import com.skillbridge.repository.TopicRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final TopicRepository topicRepository;

    public AdminController(
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            TopicRepository topicRepository
    ) {
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.topicRepository = topicRepository;
    }

    @GetMapping("/overview")
    public Map<String, Object> getSystemOverview() {
        long studentCount = userRepository.count();
        long assessmentCount = assessmentRepository.count();
        long topicCount = topicRepository.count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("status", "ACTIVE");
        stats.put("totalRegisteredUsers", studentCount);
        stats.put("totalAssessmentsCompleted", assessmentCount);
        stats.put("totalTopicsAvailable", topicCount);
        return stats;
    }

    @GetMapping("/students")
    public List<UserResponse> listStudents() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.STUDENT)
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getRole().name(),
                        u.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/assessments")
    public List<Assessment> listAllAssessments() {
        return assessmentRepository.findAllByOrderByCreatedAtDesc();
    }
}

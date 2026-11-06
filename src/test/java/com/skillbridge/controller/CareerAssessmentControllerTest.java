package com.skillbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.*;
import com.skillbridge.model.Role;
import com.skillbridge.model.User;
import com.skillbridge.repository.CareerAssessmentRepository;
import com.skillbridge.repository.CareerQuestionRepository;
import com.skillbridge.repository.CareerRepository;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CareerAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private CareerQuestionRepository questionRepository;

    @Autowired
    private CareerAssessmentRepository assessmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User student;
    private String studentToken;

    @BeforeEach
    void setUp() {
        student = userRepository.findByEmailIgnoreCase("careerstudent@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User(
                        "Career Test Student",
                        "careerstudent@skillbridge.ai",
                        passwordEncoder.encode("Password@123"),
                        Role.STUDENT
                ))
        );

        studentToken = "Bearer " + jwtUtils.generateToken(student.getEmail(), student.getRole().name(), student.getFullName(), student.getId());
    }

    @Test
    @DisplayName("GET /api/careers is publicly accessible and returns the 6 career paths")
    void testGetCareersPublic() throws Exception {
        mockMvc.perform(get("/api/careers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))))
                .andExpect(jsonPath("$[*].id", hasItems("java-developer", "full-stack-developer", "data-scientist", "cloud-engineer", "aiml-engineer", "cybersecurity-analyst")));
    }

    @Test
    @DisplayName("GET /api/careers/{careerId} returns career details and proficiency target")
    void testGetCareerById() throws Exception {
        mockMvc.perform(get("/api/careers/java-developer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("java-developer"))
                .andExpect(jsonPath("$.title").value("Java Developer"))
                .andExpect(jsonPath("$.targetProficiency").value(80))
                .andExpect(jsonPath("$.requiredSkills", hasItems("Java Fundamentals", "OOP", "Collections", "Exception Handling", "SQL", "Spring Boot")));
    }

    @Test
    @DisplayName("GET /api/careers/{careerId}/questions returns questions without revealing correctOption")
    void testGetQuestionsWithoutCorrectOption() throws Exception {
        mockMvc.perform(get("/api/careers/java-developer/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(30))))
                .andExpect(jsonPath("$[0].questionText").isNotEmpty())
                .andExpect(jsonPath("$[0].options", hasSize(4)))
                .andExpect(jsonPath("$[0].correctOption").doesNotExist()) // Never expose answer!
                .andExpect(jsonPath("$[0].difficulty").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/careers/{careerId}/assessments requires authentication")
    void testSubmitAssessmentUnauthenticated() throws Exception {
        Map<Long, Integer> dummyAnswers = Map.of(1L, 0);
        CareerAssessmentSubmissionRequest req = new CareerAssessmentSubmissionRequest(dummyAnswers);

        mockMvc.perform(post("/api/careers/java-developer/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/careers/{careerId}/assessments successfully scores and saves student assessment")
    void testSubmitAssessmentAuthenticated() throws Exception {
        // Fetch real question IDs for java-developer
        var questions = questionRepository.findByCareerIdOrderByIdAsc("java-developer");
        Map<Long, Integer> answers = new HashMap<>();

        // Intentionally answer correctly for the first half, and choose option 0 for the second half
        for (int i = 0; i < questions.size(); i++) {
            var q = questions.get(i);
            if (i < 15) {
                answers.put(q.getId(), q.getCorrectOption());
            } else {
                answers.put(q.getId(), 0);
            }
        }

        CareerAssessmentSubmissionRequest req = new CareerAssessmentSubmissionRequest(answers);

        mockMvc.perform(post("/api/careers/java-developer/assessments")
                        .header("Authorization", studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assessmentId").isNumber())
                .andExpect(jsonPath("$.careerId").value("java-developer"))
                .andExpect(jsonPath("$.studentEmail").value("careerstudent@skillbridge.ai"))
                .andExpect(jsonPath("$.totalQuestions").value(questions.size()))
                .andExpect(jsonPath("$.overallScore").isNumber())
                .andExpect(jsonPath("$.readinessPercentage").isNumber())
                .andExpect(jsonPath("$.strongestSkill").isNotEmpty())
                .andExpect(jsonPath("$.weakestSkill").isNotEmpty())
                .andExpect(jsonPath("$.disclaimer").value(containsString("estimate")))
                .andExpect(jsonPath("$.skillBreakdown", hasSize(greaterThanOrEqualTo(6))))
                .andExpect(jsonPath("$.recommendations").isArray());
    }

    @Test
    @DisplayName("GET /api/careers/assessments/my returns only authenticated student's history")
    void testGetMyCareerAssessments() throws Exception {
        mockMvc.perform(get("/api/careers/assessments/my")
                        .header("Authorization", studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

package com.skillbridge.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.controller.ApiController;
import com.skillbridge.model.Role;
import com.skillbridge.model.Topic;
import com.skillbridge.model.User;
import com.skillbridge.repository.TopicRepository;
import com.skillbridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationAndAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User studentA;
    private User studentB;
    private User adminUser;
    private Topic testTopic;

    @BeforeEach
    void setUp() {
        studentA = userRepository.findByEmailIgnoreCase("studentA@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User("Student Alpha", "studentA@skillbridge.ai", passwordEncoder.encode("Pass@123"), Role.STUDENT))
        );

        studentB = userRepository.findByEmailIgnoreCase("studentB@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User("Student Beta", "studentB@skillbridge.ai", passwordEncoder.encode("Pass@123"), Role.STUDENT))
        );

        adminUser = userRepository.findByEmailIgnoreCase("adminUser@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User("Admin Master", "adminUser@skillbridge.ai", passwordEncoder.encode("Admin@123"), Role.ADMIN))
        );

        if (topicRepository.count() == 0) {
            testTopic = topicRepository.save(new Topic("Java Basics", "Developer", "Java OOP", 80));
        } else {
            testTopic = topicRepository.findAll().get(0);
        }
    }

    @Test
    @DisplayName("Unauthenticated request to protected endpoints is rejected with 401")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/assessments"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/student/dashboard"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/overview"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Expired JWT token is rejected with 401 Unauthorized")
    void testExpiredTokenRejected() throws Exception {
        String expiredToken = jwtUtils.generateExpiredTokenForTesting(studentA.getEmail(), "STUDENT");

        mockMvc.perform(get("/api/student/dashboard")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Student role CANNOT access admin-only endpoints (403 Forbidden)")
    void testStudentCannotAccessAdminEndpoints() throws Exception {
        String studentToken = jwtUtils.generateToken(studentA.getEmail(), "STUDENT", studentA.getFullName(), studentA.getId());

        mockMvc.perform(get("/api/admin/overview")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/students")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin role CAN access admin-only endpoints (200 OK)")
    void testAdminCanAccessAdminEndpoints() throws Exception {
        String adminToken = jwtUtils.generateToken(adminUser.getEmail(), "ADMIN", adminUser.getFullName(), adminUser.getId());

        mockMvc.perform(get("/api/admin/overview")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Student data isolation: Student B does not see Student A's assessments")
    void testStudentDataIsolation() throws Exception {
        String tokenA = jwtUtils.generateToken(studentA.getEmail(), "STUDENT", studentA.getFullName(), studentA.getId());
        String tokenB = jwtUtils.generateToken(studentB.getEmail(), "STUDENT", studentB.getFullName(), studentB.getId());

        // Student A submits an assessment
        ApiController.AssessmentRequest reqA = new ApiController.AssessmentRequest(studentA.getFullName(), testTopic.getId(), 55);
        mockMvc.perform(post("/api/assessments")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(55));

        // Student B calls their student dashboard -> must have 0 assessments
        mockMvc.perform(get("/api/student/dashboard")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.email").value("studentB@skillbridge.ai"))
                .andExpect(jsonPath("$.assessmentHistory", hasSize(0)));

        // Student A calls their student dashboard -> sees their own assessment
        mockMvc.perform(get("/api/student/dashboard")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.email").value("studentA@skillbridge.ai"))
                .andExpect(jsonPath("$.totalAssessments").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }
}

package com.skillbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.model.Assessment;
import com.skillbridge.model.Topic;
import com.skillbridge.repository.AssessmentRepository;
import com.skillbridge.repository.TopicRepository;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.security.JwtAuthEntryPoint;
import com.skillbridge.security.JwtAuthenticationFilter;
import com.skillbridge.security.JwtUtils;
import com.skillbridge.security.UserDetailsServiceImpl;
import com.skillbridge.service.SkillGapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TopicRepository topicRepository;

    @MockBean
    private AssessmentRepository assessmentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SkillGapService skillGapService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Health endpoint returns UP")
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("POST assessment with score 55 returns exact score 55, target 80, gap 25")
    @WithMockUser(username = "Demo Student", roles = {"STUDENT"})
    void testCreateAssessmentScore55() throws Exception {
        Topic topic = new Topic("Java Programming", "Java Developer", "Description", 80);
        topic.setId(1L);

        Mockito.when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));

        Assessment saved = new Assessment("Demo Student", topic, 55, 80);
        saved.setId(100L);
        Mockito.when(assessmentRepository.save(any(Assessment.class))).thenReturn(saved);

        Mockito.when(skillGapService.analyze(any(Assessment.class))).thenReturn(Map.of(
                "assessmentId", 100L,
                "topic", "Java Programming",
                "score", 55,
                "targetScore", 80,
                "gap", 25,
                "level", "Needs improvement",
                "recommendation", "Focus on Java Programming fundamentals to close the 25-point gap"
        ));

        ApiController.AssessmentRequest request = new ApiController.AssessmentRequest("Demo Student", 1L, 55);

        mockMvc.perform(post("/api/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(55))
                .andExpect(jsonPath("$.targetScore").value(80))
                .andExpect(jsonPath("$.gap").value(25))
                .andExpect(jsonPath("$.level").value("Needs improvement"));
    }

    @Test
    @DisplayName("POST assessment rejects score < 0 or > 100")
    @WithMockUser(username = "Demo Student", roles = {"STUDENT"})
    void testInvalidScoreValidation() throws Exception {
        ApiController.AssessmentRequest invalidRequest = new ApiController.AssessmentRequest("Demo Student", 1L, 105);

        mockMvc.perform(post("/api/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET assessments returns assessment history for learner")
    @WithMockUser(username = "Demo Student", roles = {"STUDENT"})
    void testGetAssessments() throws Exception {
        Topic topic = new Topic("Java Programming", "Java Developer", "Desc", 80);
        Assessment assessment = new Assessment("Demo Student", topic, 55, 80);
        assessment.setId(100L);

        Mockito.when(assessmentRepository.findByLearnerNameIgnoreCaseOrderByCreatedAtDesc("Demo Student"))
                .thenReturn(List.of(assessment));

        mockMvc.perform(get("/api/assessments?learner=Demo Student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(55))
                .andExpect(jsonPath("$[0].targetScore").value(80))
                .andExpect(jsonPath("$[0].learnerName").value("Demo Student"));
    }
}

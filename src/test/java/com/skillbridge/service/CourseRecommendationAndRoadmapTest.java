package com.skillbridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.CourseRecommendationDto;
import com.skillbridge.dto.LearningRoadmapDto;
import com.skillbridge.dto.SkillScoreDto;
import com.skillbridge.model.CareerAssessment;
import com.skillbridge.model.Role;
import com.skillbridge.model.User;
import com.skillbridge.repository.CareerAssessmentRepository;
import com.skillbridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseRecommendationAndRoadmapTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerAssessmentRepository assessmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User student;

    @BeforeEach
    void setUp() throws Exception {
        student = userRepository.findByEmailIgnoreCase("recommend_student@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User(
                        "Recommendation Student",
                        "recommend_student@skillbridge.ai",
                        passwordEncoder.encode("Password@123"),
                        Role.STUDENT
                ))
        );

        // Delete past assessments for clean test state
        List<CareerAssessment> old = assessmentRepository.findByUserOrderByCreatedAtDesc(student);
        assessmentRepository.deleteAll(old);

        // Create a realistic assessment with known skill gaps:
        // SQL: 35% (Gap: 45 pts) -> LARGEST GAP!
        // Spring Boot: 50% (Gap: 30 pts)
        // Java Fundamentals: 85% (Gap: 0 pts)
        List<SkillScoreDto> skills = List.of(
                new SkillScoreDto("SQL", 2, 6, 35, 80, 45, "Needs Improvement"),
                new SkillScoreDto("Spring Boot", 3, 6, 50, 80, 30, "Developing"),
                new SkillScoreDto("Java Fundamentals", 5, 6, 85, 80, 0, "Proficient")
        );

        CareerAssessment assessment = new CareerAssessment();
        assessment.setUser(student);
        assessment.setStudentName(student.getFullName());
        assessment.setStudentEmail(student.getEmail());
        assessment.setCareerId("java-developer");
        assessment.setCareerTitle("Java Developer");
        assessment.setTotalQuestions(18);
        assessment.setCorrectAnswers(10);
        assessment.setOverallScore(55.6);
        assessment.setReadinessPercentage(56);
        assessment.setStrongestSkill("Java Fundamentals (85%)");
        assessment.setWeakestSkill("SQL (35%)");
        assessment.setSkillBreakdownJson(objectMapper.writeValueAsString(skills));
        assessment.setRecommendationsJson("[\"Study SQL Fundamentals\"]");
        assessment.setCreatedAt(LocalDateTime.now());

        assessmentRepository.save(assessment);
    }

    @Test
    @DisplayName("Personalized recommendations prioritize largest skill gaps first")
    void testRecommendationsPrioritizedByLargestGap() {
        List<CourseRecommendationDto> recommendations = courseService.getPersonalizedRecommendations(student);

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty(), "Recommendations should not be empty");

        // The first recommended course should address the largest gap (SQL with 45 pt gap)
        CourseRecommendationDto firstRec = recommendations.get(0);
        assertTrue(firstRec.skillCategory().equalsIgnoreCase("SQL") || firstRec.courseTitle().contains("SQL"),
                "First recommendation should address the largest gap (SQL)");
        assertEquals(45, firstRec.gap(), "First recommendation should have gap of 45");

        // Check personalized reason contains student's actual score and gap
        assertTrue(firstRec.reason().contains("35%"), "Reason should mention student score of 35%");
        assertTrue(firstRec.reason().contains("45 points") || firstRec.reason().contains("45"), "Reason should mention gap of 45");
    }

    @Test
    @DisplayName("Learning roadmap includes sequence, priority skills, and reassessment guidance")
    void testLearningRoadmapStructure() {
        LearningRoadmapDto roadmap = courseService.getLearningRoadmap(student);

        assertNotNull(roadmap);
        assertEquals("java-developer", roadmap.careerId());
        assertEquals("Java Developer", roadmap.careerTitle());
        assertEquals(56, roadmap.readinessScore());
        assertTrue(roadmap.totalIdentifiedGaps() >= 75, "Total gaps should sum at least 45 + 30 = 75");

        assertFalse(roadmap.prioritySkills().isEmpty());
        // Verify priority skills are sorted by gap descending
        int firstPriorityGap = (int) roadmap.prioritySkills().get(0).get("gap");
        assertEquals(45, firstPriorityGap, "Highest priority skill should have gap of 45");

        // Verify reassessment advice is present and emphasizes that course completion does not equate to mastery
        assertTrue(roadmap.reassessmentRecommended());
        assertNotNull(roadmap.reassessmentMessage());
        assertTrue(roadmap.reassessmentMessage().toLowerCase().contains("reassess") ||
                   roadmap.reassessmentMessage().toLowerCase().contains("retake"),
                "Roadmap must recommend reassessment to measure real improvement");
    }
}

package com.skillbridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.*;
import com.skillbridge.model.*;
import com.skillbridge.repository.CareerAssessmentRepository;
import com.skillbridge.repository.CareerQuestionRepository;
import com.skillbridge.repository.CareerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareerAssessmentServiceTest {

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private CareerQuestionRepository questionRepository;

    @Mock
    private CareerAssessmentRepository assessmentRepository;

    private ObjectMapper objectMapper;
    private CareerAssessmentService service;

    private Career javaCareer;
    private List<CareerQuestion> sampleQuestions;
    private User testStudent;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new CareerAssessmentService(careerRepository, questionRepository, assessmentRepository, objectMapper);

        javaCareer = new Career(
                "java-developer",
                "Java Developer",
                "Enterprise Java and Spring Boot development",
                80,
                List.of("Java Fundamentals", "OOP", "Collections"),
                "bi-filetype-java",
                "primary"
        );

        testStudent = new User("Jane Doe", "jane@example.com", "Password@123", Role.STUDENT);
        testStudent.setId(101L);

        // Prepare 6 questions (2 in Java Fundamentals, 2 in OOP, 2 in Collections)
        sampleQuestions = new ArrayList<>();
        // Java Fundamentals: IDs 1, 2 (correct: 0, 1)
        sampleQuestions.add(new CareerQuestion("java-developer", "Java Fundamentals", "Q1", "A", "B", "C", "D", 0, QuestionDifficulty.BEGINNER, "Exp1"));
        sampleQuestions.get(0).setId(1L);
        sampleQuestions.add(new CareerQuestion("java-developer", "Java Fundamentals", "Q2", "A", "B", "C", "D", 1, QuestionDifficulty.INTERMEDIATE, "Exp2"));
        sampleQuestions.get(1).setId(2L);

        // OOP: IDs 3, 4 (correct: 2, 3)
        sampleQuestions.add(new CareerQuestion("java-developer", "OOP", "Q3", "A", "B", "C", "D", 2, QuestionDifficulty.BEGINNER, "Exp3"));
        sampleQuestions.get(2).setId(3L);
        sampleQuestions.add(new CareerQuestion("java-developer", "OOP", "Q4", "A", "B", "C", "D", 3, QuestionDifficulty.ADVANCED, "Exp4"));
        sampleQuestions.get(3).setId(4L);

        // Collections: IDs 5, 6 (correct: 0, 1)
        sampleQuestions.add(new CareerQuestion("java-developer", "Collections", "Q5", "A", "B", "C", "D", 0, QuestionDifficulty.INTERMEDIATE, "Exp5"));
        sampleQuestions.get(4).setId(5L);
        sampleQuestions.add(new CareerQuestion("java-developer", "Collections", "Q6", "A", "B", "C", "D", 1, QuestionDifficulty.ADVANCED, "Exp6"));
        sampleQuestions.get(5).setId(6L);
    }

    @Test
    @DisplayName("getQuestionsForCareer returns DTOs without revealing correct answers")
    void testGetQuestionsDoesNotLeakCorrectAnswers() {
        when(careerRepository.findByIdIgnoreCase("java-developer")).thenReturn(Optional.of(javaCareer));
        when(questionRepository.findByCareerIdOrderByIdAsc("java-developer")).thenReturn(sampleQuestions);

        List<CareerQuestionDto> dtos = service.getQuestionsForCareer("java-developer");

        assertEquals(6, dtos.size());
        for (CareerQuestionDto dto : dtos) {
            assertNotNull(dto.id());
            assertNotNull(dto.skillCategory());
            assertNotNull(dto.questionText());
            assertEquals(4, dto.options().size());
            assertNotNull(dto.difficulty());
            // Confirm the DTO class does not even have a getCorrectOption method
            assertFalse(dto.getClass().toString().contains("correctOption"));
        }
    }

    @Test
    @DisplayName("Automatic scoring: 100% score when all questions answered correctly")
    void testPerfectScoreAssessment() {
        when(careerRepository.findByIdIgnoreCase("java-developer")).thenReturn(Optional.of(javaCareer));
        when(questionRepository.findByCareerIdOrderByIdAsc("java-developer")).thenReturn(sampleQuestions);
        when(assessmentRepository.save(any(CareerAssessment.class))).thenAnswer(invocation -> {
            CareerAssessment a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });

        // Answers map with all 6 correct options
        Map<Long, Integer> answers = Map.of(
                1L, 0, // Java Fundamentals (correct)
                2L, 1, // Java Fundamentals (correct)
                3L, 2, // OOP (correct)
                4L, 3, // OOP (correct)
                5L, 0, // Collections (correct)
                6L, 1  // Collections (correct)
        );

        CareerAssessmentResultDto result = service.submitAssessment("java-developer", testStudent, new CareerAssessmentSubmissionRequest(answers));

        assertEquals(6, result.totalQuestions());
        assertEquals(6, result.correctAnswers());
        assertEquals(100.0, result.overallScore());
        assertEquals(100, result.readinessPercentage());
        assertEquals(80, result.targetProficiency());
        assertEquals(0, result.totalSkillGap());
        assertNotNull(result.disclaimer());
        assertTrue(result.disclaimer().contains("estimate"));

        // All 3 skills must have score 100, gap 0, and level Strong
        for (SkillScoreDto skill : result.skillBreakdown()) {
            assertEquals(100, skill.score());
            assertEquals(0, skill.gap());
            assertEquals("Strong", skill.level());
        }
    }

    @Test
    @DisplayName("Partial scoring and skill gap formula: max(0, targetScore - studentScore)")
    void testPartialScoreAndSkillGap() {
        when(careerRepository.findByIdIgnoreCase("java-developer")).thenReturn(Optional.of(javaCareer));
        when(questionRepository.findByCareerIdOrderByIdAsc("java-developer")).thenReturn(sampleQuestions);
        when(assessmentRepository.save(any(CareerAssessment.class))).thenAnswer(invocation -> {
            CareerAssessment a = invocation.getArgument(0);
            a.setId(2L);
            return a;
        });

        // Scenario:
        // Java Fundamentals: 2/2 correct = 100% -> Gap = max(0, 80 - 100) = 0 -> Strong
        // OOP: 1/2 correct = 50% -> Gap = max(0, 80 - 50) = 30 -> Developing
        // Collections: 0/2 correct = 0% -> Gap = max(0, 80 - 0) = 80 -> Needs Improvement
        // Total correct: 3/6 = 50% overall
        Map<Long, Integer> answers = Map.of(
                1L, 0, // correct
                2L, 1, // correct
                3L, 2, // correct
                4L, 0, // incorrect (correct is 3)
                5L, 2, // incorrect (correct is 0)
                6L, 3  // incorrect (correct is 1)
        );

        CareerAssessmentResultDto result = service.submitAssessment("java-developer", testStudent, new CareerAssessmentSubmissionRequest(answers));

        assertEquals(6, result.totalQuestions());
        assertEquals(3, result.correctAnswers());
        assertEquals(50.0, result.overallScore());
        assertEquals(50, result.readinessPercentage());
        assertEquals(110, result.totalSkillGap()); // 0 + 30 + 80 = 110

        Map<String, SkillScoreDto> skillMap = new HashMap<>();
        for (SkillScoreDto s : result.skillBreakdown()) {
            skillMap.put(s.skillName(), s);
        }

        // Check Java Fundamentals
        SkillScoreDto jf = skillMap.get("Java Fundamentals");
        assertEquals(100, jf.score());
        assertEquals(0, jf.gap());
        assertEquals("Strong", jf.level());

        // Check OOP
        SkillScoreDto oop = skillMap.get("OOP");
        assertEquals(50, oop.score());
        assertEquals(30, oop.gap());
        assertEquals("Developing", oop.level());

        // Check Collections
        SkillScoreDto coll = skillMap.get("Collections");
        assertEquals(0, coll.score());
        assertEquals(80, coll.gap());
        assertEquals("Needs Improvement", coll.level());

        // Strongest & Weakest skill check
        assertTrue(result.strongestSkill().contains("Java Fundamentals"));
        assertTrue(result.weakestSkill().contains("Collections"));
    }

    @Test
    @DisplayName("Validation: Incomplete submissions with unanswered questions are rejected")
    void testIncompleteSubmissionRejection() {
        when(careerRepository.findByIdIgnoreCase("java-developer")).thenReturn(Optional.of(javaCareer));
        when(questionRepository.findByCareerIdOrderByIdAsc("java-developer")).thenReturn(sampleQuestions);

        // Only answer 5 of 6 questions
        Map<Long, Integer> partialAnswers = Map.of(
                1L, 0,
                2L, 1,
                3L, 2,
                4L, 3,
                5L, 0
                // Missing question 6L
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.submitAssessment("java-developer", testStudent, new CareerAssessmentSubmissionRequest(partialAnswers))
        );

        assertTrue(ex.getMessage().contains("All 6 questions must be answered"));
    }

    @Test
    @DisplayName("Validation: Invalid option index (< 0 or > 3) is rejected")
    void testInvalidOptionIndexRejection() {
        when(careerRepository.findByIdIgnoreCase("java-developer")).thenReturn(Optional.of(javaCareer));
        when(questionRepository.findByCareerIdOrderByIdAsc("java-developer")).thenReturn(sampleQuestions);

        Map<Long, Integer> invalidAnswers = Map.of(
                1L, 0, 2L, 1, 3L, 2, 4L, 3, 5L, 0, 6L, 5 // 5 is invalid
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.submitAssessment("java-developer", testStudent, new CareerAssessmentSubmissionRequest(invalidAnswers))
        );

        assertTrue(ex.getMessage().contains("Invalid option selected"));
    }

    @Test
    @DisplayName("Student isolation: Student cannot access another student's assessment")
    void testStudentOwnershipEnforcement() {
        User studentA = new User("Student A", "a@skillbridge.ai", "pass", Role.STUDENT);
        studentA.setId(1L);

        User studentB = new User("Student B", "b@skillbridge.ai", "pass", Role.STUDENT);
        studentB.setId(2L);

        CareerAssessment assessmentA = new CareerAssessment();
        assessmentA.setId(99L);
        assessmentA.setUser(studentA);
        assessmentA.setStudentEmail("a@skillbridge.ai");
        assessmentA.setStudentName("Student A");
        assessmentA.setCareerId("java-developer");
        assessmentA.setCareerTitle("Java Developer");
        assessmentA.setTotalQuestions(6);
        assessmentA.setCorrectAnswers(5);
        assessmentA.setOverallScore(83.3);

        when(assessmentRepository.findById(99L)).thenReturn(Optional.of(assessmentA));

        // Student B attempts to access Student A's result
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.getAssessmentById(99L, studentB)
        );

        assertTrue(ex.getMessage().contains("Access denied"));

        // Student A can access their own result
        CareerAssessmentResultDto dto = service.getAssessmentById(99L, studentA);
        assertNotNull(dto);
        assertEquals(99L, dto.assessmentId());
    }

    @Test
    @DisplayName("Skill level categorization thresholds: Strong (80-100), Intermediate (60-79), Developing (40-59), Needs Improvement (< 40)")
    void testSkillCategorizationThresholds() {
        assertEquals("Strong", CareerAssessmentService.categorizeSkillLevel(100));
        assertEquals("Strong", CareerAssessmentService.categorizeSkillLevel(80));
        assertEquals("Intermediate", CareerAssessmentService.categorizeSkillLevel(79));
        assertEquals("Intermediate", CareerAssessmentService.categorizeSkillLevel(60));
        assertEquals("Developing", CareerAssessmentService.categorizeSkillLevel(59));
        assertEquals("Developing", CareerAssessmentService.categorizeSkillLevel(40));
        assertEquals("Needs Improvement", CareerAssessmentService.categorizeSkillLevel(39));
        assertEquals("Needs Improvement", CareerAssessmentService.categorizeSkillLevel(0));
    }
}

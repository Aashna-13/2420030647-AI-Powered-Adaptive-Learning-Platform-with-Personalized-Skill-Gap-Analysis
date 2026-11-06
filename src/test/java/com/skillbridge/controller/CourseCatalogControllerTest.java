package com.skillbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.QuizSubmissionRequest;
import com.skillbridge.model.*;
import com.skillbridge.repository.*;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CourseCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonQuizQuestionRepository quizQuestionRepository;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User student;
    private String studentToken;

    @BeforeEach
    void setUp() {
        student = userRepository.findByEmailIgnoreCase("lms_student@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User(
                        "LMS Test Student",
                        "lms_student@skillbridge.ai",
                        passwordEncoder.encode("Password@123"),
                        Role.STUDENT
                ))
        );
        studentToken = "Bearer " + jwtUtils.generateToken(student.getEmail(), student.getRole().name(), student.getFullName(), student.getId());
    }

    @Test
    @DisplayName("GET /api/courses returns public course catalog with at least 32 courses across 6 careers")
    void testGetPublicCourses() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(32))))
                .andExpect(jsonPath("$[*].careerId", hasItems("java-developer", "full-stack-developer", "data-scientist", "cloud-engineer", "aiml-engineer", "cybersecurity-analyst")));
    }

    @Test
    @DisplayName("GET /api/courses with career filter returns only courses for that career")
    void testGetCoursesFilterByCareer() throws Exception {
        mockMvc.perform(get("/api/courses").param("careerId", "java-developer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))))
                .andExpect(jsonPath("$[*].careerId", everyItem(equalTo("java-developer"))));
    }

    @Test
    @DisplayName("GET /api/courses with search query returns matching courses")
    void testGetCoursesSearchQuery() throws Exception {
        mockMvc.perform(get("/api/courses").param("query", "Spring Boot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", containsString("Spring Boot")));
    }

    @Test
    @DisplayName("GET /api/courses/{idOrSlug} returns course details with syllabus lessons")
    void testGetCourseDetails() throws Exception {
        mockMvc.perform(get("/api/courses/java-fundamentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course.slug").value("java-fundamentals"))
                .andExpect(jsonPath("$.course.title").value("Java Fundamentals"))
                .andExpect(jsonPath("$.lessons", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.lessons[0].title", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/courses/{slug}/enroll requires authentication")
    void testEnrollRequiresAuth() throws Exception {
        mockMvc.perform(post("/api/courses/java-fundamentals/enroll"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/courses/{slug}/enroll enrolls authenticated student and prevents duplicate entries")
    void testEnrollStudentAndDuplicatePrevention() throws Exception {
        // First enrollment
        mockMvc.perform(post("/api/courses/java-fundamentals/enroll")
                        .header("Authorization", studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentStatus").value(anyOf(equalTo("ENROLLED"), equalTo("IN_PROGRESS"))));

        Course course = courseRepository.findBySlugIgnoreCase("java-fundamentals").orElseThrow();
        assertTrue(enrollmentRepository.existsByUserAndCourse(student, course));

        // Duplicate enrollment attempt - should be idempotent and return 200 without error
        mockMvc.perform(post("/api/courses/java-fundamentals/enroll")
                        .header("Authorization", studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("java-fundamentals"));

        // Only one enrollment record in database
        List<CourseEnrollment> enrollments = enrollmentRepository.findByUserOrderByEnrolledAtDesc(student);
        long count = enrollments.stream().filter(e -> e.getCourse().getId().equals(course.getId())).count();
        assertEquals(1, count, "Duplicate enrollment should not be created in DB");
    }

    @Test
    @DisplayName("GET /api/courses/my-enrollments returns only the authenticated student's courses")
    void testGetMyEnrollmentsIsolation() throws Exception {
        // Enroll student in a course
        mockMvc.perform(post("/api/courses/sql-fundamentals/enroll")
                        .header("Authorization", studentToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/courses/my-enrollments")
                        .header("Authorization", studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].slug", hasItem("sql-fundamentals")));
    }

    @Test
    @DisplayName("GET /api/courses/{courseId}/lessons/{lessonId} returns full lesson theory content and nav links")
    void testGetLessonDetails() throws Exception {
        Course course = courseRepository.findBySlugIgnoreCase("java-fundamentals").orElseThrow();
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
        assertFalse(lessons.isEmpty());
        Lesson firstLesson = lessons.get(0);

        mockMvc.perform(get("/api/courses/" + course.getId() + "/lessons/" + firstLesson.getId())
                        .header("Authorization", studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(firstLesson.getId()))
                .andExpect(jsonPath("$.title").value(firstLesson.getTitle()))
                .andExpect(jsonPath("$.learningObjectives", notNullValue()))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.codeExamples", notNullValue()))
                .andExpect(jsonPath("$.importantConcepts", notNullValue()))
                .andExpect(jsonPath("$.summary", notNullValue()))
                .andExpect(jsonPath("$.nextLessonId", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/courses/lessons/{lessonId}/complete updates progress and course percentage")
    void testMarkLessonComplete() throws Exception {
        Course course = courseRepository.findBySlugIgnoreCase("java-fundamentals").orElseThrow();
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
        Lesson firstLesson = lessons.get(0);

        mockMvc.perform(post("/api/courses/lessons/" + firstLesson.getId() + "/complete")
                        .header("Authorization", studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonCompleted").value(true))
                .andExpect(jsonPath("$.courseProgressPercentage", greaterThan(0)));

        assertTrue(lessonProgressRepository.findByUserAndLesson(student, firstLesson).map(LessonProgress::isCompleted).orElse(false));
    }

    @Test
    @DisplayName("GET /api/courses/lessons/{lessonId}/quiz returns questions without exposing correct answer")
    void testGetLessonQuizWithoutAnswers() throws Exception {
        Course course = courseRepository.findBySlugIgnoreCase("java-fundamentals").orElseThrow();
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
        Lesson firstLesson = lessons.get(0);

        mockMvc.perform(get("/api/courses/lessons/" + firstLesson.getId() + "/quiz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].questionText", notNullValue()))
                .andExpect(jsonPath("$[0].options", hasSize(4)))
                .andExpect(jsonPath("$[0].correctOption").doesNotExist())
                .andExpect(jsonPath("$[0].explanation").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/courses/lessons/{lessonId}/quiz grades answers and provides explanations")
    void testSubmitLessonQuiz() throws Exception {
        Course course = courseRepository.findBySlugIgnoreCase("java-fundamentals").orElseThrow();
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
        Lesson firstLesson = lessons.get(0);
        List<LessonQuizQuestion> questions = quizQuestionRepository.findByLessonIdOrderByQuestionOrderAsc(firstLesson.getId());

        // Build valid answers with all correct answers
        Map<Long, Integer> answers = new HashMap<>();
        for (LessonQuizQuestion q : questions) {
            answers.put(q.getId(), q.getCorrectOption());
        }

        QuizSubmissionRequest request = new QuizSubmissionRequest(answers);

        mockMvc.perform(post("/api/courses/lessons/" + firstLesson.getId() + "/quiz")
                        .header("Authorization", studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuestions").value(questions.size()))
                .andExpect(jsonPath("$.correctAnswers").value(questions.size()))
                .andExpect(jsonPath("$.scorePercentage").value(100))
                .andExpect(jsonPath("$.passed").value(true))
                .andExpect(jsonPath("$.lessonCompleted").value(true))
                .andExpect(jsonPath("$.questionResults", hasSize(questions.size())))
                .andExpect(jsonPath("$.questionResults[0].explanation", notNullValue()));

        // Verify attempt persisted in DB
        assertTrue(quizAttemptRepository.findTopByUserAndLessonOrderByAttemptedAtDesc(student, firstLesson).isPresent());
    }
}

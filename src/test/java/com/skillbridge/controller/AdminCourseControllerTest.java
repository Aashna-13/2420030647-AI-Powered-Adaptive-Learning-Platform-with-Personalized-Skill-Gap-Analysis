package com.skillbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.CourseCreateUpdateRequest;
import com.skillbridge.dto.LessonCreateUpdateRequest;
import com.skillbridge.model.Course;
import com.skillbridge.model.QuestionDifficulty;
import com.skillbridge.model.Role;
import com.skillbridge.model.User;
import com.skillbridge.repository.CourseRepository;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User student;
    private User admin;
    private String studentToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        student = userRepository.findByEmailIgnoreCase("regular_student@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User(
                        "Regular Student",
                        "regular_student@skillbridge.ai",
                        passwordEncoder.encode("Password@123"),
                        Role.STUDENT
                ))
        );
        studentToken = "Bearer " + jwtUtils.generateToken(student.getEmail(), student.getRole().name(), student.getFullName(), student.getId());

        admin = userRepository.findByEmailIgnoreCase("admin_tester@skillbridge.ai").orElseGet(() ->
                userRepository.save(new User(
                        "Admin Tester",
                        "admin_tester@skillbridge.ai",
                        passwordEncoder.encode("Admin@123"),
                        Role.ADMIN
                ))
        );
        adminToken = "Bearer " + jwtUtils.generateToken(admin.getEmail(), admin.getRole().name(), admin.getFullName(), admin.getId());
    }

    @Test
    @DisplayName("GET /api/admin/courses requires ADMIN role; student gets 403 Forbidden")
    void testAdminCoursesForbiddenForStudents() throws Exception {
        mockMvc.perform(get("/api/admin/courses")
                        .header("Authorization", studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/courses returns all courses for ADMIN role")
    void testAdminCoursesAccessibleForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/courses")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(32))));
    }

    @Test
    @DisplayName("POST /api/admin/courses allows ADMIN to create a new course")
    void testAdminCreateCourse() throws Exception {
        CourseCreateUpdateRequest request = new CourseCreateUpdateRequest(
                "quantum-computing-fundamentals",
                "Quantum Computing Fundamentals",
                "Explore qubits, superposition, quantum entanglement, and quantum algorithms.",
                "aiml-engineer",
                "AI/ML Engineer",
                "Quantum Computing",
                QuestionDifficulty.ADVANCED,
                "5 hours",
                "bi-cpu-fill",
                "danger",
                true
        );

        mockMvc.perform(post("/api/admin/courses")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("quantum-computing-fundamentals"))
                .andExpect(jsonPath("$.title").value("Quantum Computing Fundamentals"));
    }

    @Test
    @DisplayName("POST /api/admin/courses/{courseId}/lessons allows ADMIN to add lesson to a course")
    void testAdminAddLesson() throws Exception {
        Course course = courseRepository.findBySlugIgnoreCase("java-fundamentals").orElseThrow();

        LessonCreateUpdateRequest request = new LessonCreateUpdateRequest(
                "Advanced Java Concurrency and Threads",
                3,
                30,
                "• Understand Java Memory Model\n• Learn thread locks and CAS",
                "Concurrency enables multiple tasks to execute simultaneously.",
                "Threads allow simultaneous execution within a process.",
                "Thread t = new Thread(() -> System.out.println(\"Hello\"));",
                "• Thread safety requires synchronization\n• Avoid deadlocks",
                "Concurrency requires careful synchronization to avoid race conditions.",
                "1. What is a deadlock?"
        );

        mockMvc.perform(post("/api/admin/courses/" + course.getId() + "/lessons")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Advanced Java Concurrency and Threads"));
    }
}

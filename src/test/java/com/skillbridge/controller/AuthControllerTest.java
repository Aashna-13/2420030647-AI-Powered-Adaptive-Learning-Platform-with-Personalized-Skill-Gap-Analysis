package com.skillbridge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.LoginRequest;
import com.skillbridge.dto.RegisterRequest;
import com.skillbridge.model.Role;
import com.skillbridge.model.User;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Ensure student exists
        if (!userRepository.existsByEmailIgnoreCase("teststudent@skillbridge.ai")) {
            userRepository.save(new User(
                    "Test Student",
                    "teststudent@skillbridge.ai",
                    passwordEncoder.encode("Student@123"),
                    Role.STUDENT
            ));
        }
    }

    @Test
    @DisplayName("Successful registration generates JWT, assigns STUDENT role, and does not expose password")
    void testSuccessfulRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Alice Wonder",
                "alice.wonder@skillbridge.ai",
                "Password@123",
                "Password@123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.fullName").value("Alice Wonder"))
                .andExpect(jsonPath("$.email").value("alice.wonder@skillbridge.ai"))
                .andExpect(jsonPath("$.password").doesNotExist()); // Never expose password in response

        // Verify password in DB is hashed and not plain-text
        User savedUser = userRepository.findByEmailIgnoreCase("alice.wonder@skillbridge.ai").orElseThrow();
        assertTrue(passwordEncoder.matches("Password@123", savedUser.getPassword()));
    }

    @Test
    @DisplayName("Registration rejects duplicate email")
    void testDuplicateEmailRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Duplicate User",
                "teststudent@skillbridge.ai",
                "Password@123",
                "Password@123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already registered")));
    }

    @Test
    @DisplayName("Registration rejects password mismatch")
    void testPasswordMismatchRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Bob Mismatch",
                "bob.mismatch@skillbridge.ai",
                "Password@123",
                "DifferentPassword@456"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Passwords do not match")));
    }

    @Test
    @DisplayName("Registration rejects short password (< 6 chars)")
    void testShortPasswordRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Short Pwd",
                "short.pwd@skillbridge.ai",
                "123",
                "123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Successful login returns JWT token and student details")
    void testSuccessfulLogin() throws Exception {
        LoginRequest request = new LoginRequest("teststudent@skillbridge.ai", "Student@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.email").value("teststudent@skillbridge.ai"))
                .andExpect(jsonPath("$.fullName").value("Test Student"));
    }

    @Test
    @DisplayName("Login with invalid password returns 401 Unauthorized")
    void testInvalidLogin() throws Exception {
        LoginRequest request = new LoginRequest("teststudent@skillbridge.ai", "WrongPassword!999");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));
    }
}

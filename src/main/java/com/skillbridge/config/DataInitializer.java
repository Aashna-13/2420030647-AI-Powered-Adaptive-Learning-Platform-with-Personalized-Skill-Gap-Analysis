package com.skillbridge.config;

import com.skillbridge.model.Assessment;
import com.skillbridge.model.Role;
import com.skillbridge.model.Topic;
import com.skillbridge.model.User;
import com.skillbridge.repository.AssessmentRepository;
import com.skillbridge.repository.TopicRepository;
import com.skillbridge.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedInitialData(
            TopicRepository topicRepository,
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Seed Topics if empty
            if (topicRepository.count() == 0) {
                topicRepository.save(new Topic("Java Programming", "Java Developer", "Core Java, OOP, collections, exceptions", 80));
                topicRepository.save(new Topic("SQL & Databases", "Data Analyst / Backend Developer", "SQL queries, joins, aggregation, schema basics", 80));
                topicRepository.save(new Topic("Web Development", "Full Stack Developer", "HTML, CSS, JavaScript and web fundamentals", 80));
                topicRepository.save(new Topic("Data Structures", "Software Engineer", "Arrays, strings, searching, sorting and problem solving", 80));
                topicRepository.save(new Topic("Spring Boot", "Java Backend Developer", "REST APIs, dependency injection and persistence", 80));
            }

            // Seed Default Student User if not present
            User demoStudent = userRepository.findByEmailIgnoreCase("student@skillbridge.ai").orElseGet(() -> {
                User student = new User(
                        "Demo Student",
                        "student@skillbridge.ai",
                        passwordEncoder.encode("Student@123"),
                        Role.STUDENT
                );
                return userRepository.save(student);
            });

            // Seed Default Admin User if not present
            userRepository.findByEmailIgnoreCase("admin@skillbridge.ai").orElseGet(() -> {
                User admin = new User(
                        "Platform Administrator",
                        "admin@skillbridge.ai",
                        passwordEncoder.encode("Admin@123"),
                        Role.ADMIN
                );
                return userRepository.save(admin);
            });

            // Link existing legacy assessments to demo student so history is preserved
            List<Assessment> legacyAssessments = assessmentRepository.findByLearnerNameIgnoreCaseOrderByCreatedAtDesc("Demo Student");
            for (Assessment a : legacyAssessments) {
                if (a.getUser() == null) {
                    a.setUser(demoStudent);
                    assessmentRepository.save(a);
                }
            }
        };
    }
}


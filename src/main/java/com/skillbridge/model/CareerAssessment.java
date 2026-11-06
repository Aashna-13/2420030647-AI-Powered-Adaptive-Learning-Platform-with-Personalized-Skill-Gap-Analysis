package com.skillbridge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "career_assessments", indexes = {
        @Index(name = "idx_ca_user", columnList = "user_id"),
        @Index(name = "idx_ca_email", columnList = "studentEmail"),
        @Index(name = "idx_ca_career", columnList = "careerId")
})
public class CareerAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 120)
    private String studentName;

    @Column(nullable = false, length = 120)
    private String studentEmail;

    @Column(nullable = false, length = 64)
    private String careerId;

    @Column(nullable = false, length = 120)
    private String careerTitle;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private double overallScore;

    @Column(nullable = false)
    private int readinessPercentage;

    @Column(length = 100)
    private String strongestSkill;

    @Column(length = 100)
    private String weakestSkill;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String skillBreakdownJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String recommendationsJson;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public CareerAssessment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCareerId() { return careerId; }
    public void setCareerId(String careerId) { this.careerId = careerId; }

    public String getCareerTitle() { return careerTitle; }
    public void setCareerTitle(String careerTitle) { this.careerTitle = careerTitle; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }

    public int getReadinessPercentage() { return readinessPercentage; }
    public void setReadinessPercentage(int readinessPercentage) { this.readinessPercentage = readinessPercentage; }

    public String getStrongestSkill() { return strongestSkill; }
    public void setStrongestSkill(String strongestSkill) { this.strongestSkill = strongestSkill; }

    public String getWeakestSkill() { return weakestSkill; }
    public void setWeakestSkill(String weakestSkill) { this.weakestSkill = weakestSkill; }

    public String getSkillBreakdownJson() { return skillBreakdownJson; }
    public void setSkillBreakdownJson(String skillBreakdownJson) { this.skillBreakdownJson = skillBreakdownJson; }

    public String getRecommendationsJson() { return recommendationsJson; }
    public void setRecommendationsJson(String recommendationsJson) { this.recommendationsJson = recommendationsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

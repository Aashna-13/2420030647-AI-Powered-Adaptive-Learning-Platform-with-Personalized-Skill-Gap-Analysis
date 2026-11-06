package com.skillbridge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_course_career", columnList = "careerId"),
        @Index(name = "idx_course_skill", columnList = "skillCategory"),
        @Index(name = "idx_course_slug", columnList = "slug")
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1500)
    private String description;

    @Column(nullable = false, length = 64)
    private String careerId;

    @Column(nullable = false, length = 120)
    private String careerTitle;

    @Column(nullable = false, length = 100)
    private String skillCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionDifficulty difficulty = QuestionDifficulty.BEGINNER;

    @Column(nullable = false, length = 50)
    private String estimatedDuration = "3 hours";

    @Column(length = 60)
    private String icon = "bi-journal-code";

    @Column(length = 30)
    private String badgeColor = "primary";

    @Column(nullable = false)
    private boolean published = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Course() {}

    public Course(String slug, String title, String description, String careerId, String careerTitle,
                  String skillCategory, QuestionDifficulty difficulty, String estimatedDuration,
                  String icon, String badgeColor) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.careerId = careerId;
        this.careerTitle = careerTitle;
        this.skillCategory = skillCategory;
        this.difficulty = difficulty != null ? difficulty : QuestionDifficulty.BEGINNER;
        this.estimatedDuration = estimatedDuration != null ? estimatedDuration : "3 hours";
        this.icon = icon != null ? icon : "bi-journal-code";
        this.badgeColor = badgeColor != null ? badgeColor : "primary";
        this.published = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCareerId() { return careerId; }
    public void setCareerId(String careerId) { this.careerId = careerId; }

    public String getCareerTitle() { return careerTitle; }
    public void setCareerTitle(String careerTitle) { this.careerTitle = careerTitle; }

    public String getSkillCategory() { return skillCategory; }
    public void setSkillCategory(String skillCategory) { this.skillCategory = skillCategory; }

    public QuestionDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(QuestionDifficulty difficulty) { this.difficulty = difficulty; }

    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getBadgeColor() { return badgeColor; }
    public void setBadgeColor(String badgeColor) { this.badgeColor = badgeColor; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

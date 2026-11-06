package com.skillbridge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lessons", indexes = {
        @Index(name = "idx_lesson_course", columnList = "course_id"),
        @Index(name = "idx_lesson_order", columnList = "course_id, lessonOrder")
})
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private int lessonOrder = 1;

    @Column(nullable = false)
    private int estimatedMinutes = 20;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String learningObjectives;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String codeExamples;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String importantConcepts;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String practiceQuestions;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Lesson() {}

    public Lesson(Course course, String title, int lessonOrder, int estimatedMinutes,
                  String learningObjectives, String introduction, String content,
                  String codeExamples, String importantConcepts, String summary,
                  String practiceQuestions) {
        this.course = course;
        this.title = title;
        this.lessonOrder = lessonOrder;
        this.estimatedMinutes = estimatedMinutes;
        this.learningObjectives = learningObjectives;
        this.introduction = introduction;
        this.content = content;
        this.codeExamples = codeExamples;
        this.importantConcepts = importantConcepts;
        this.summary = summary;
        this.practiceQuestions = practiceQuestions;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getLessonOrder() { return lessonOrder; }
    public void setLessonOrder(int lessonOrder) { this.lessonOrder = lessonOrder; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getLearningObjectives() { return learningObjectives; }
    public void setLearningObjectives(String learningObjectives) { this.learningObjectives = learningObjectives; }

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCodeExamples() { return codeExamples; }
    public void setCodeExamples(String codeExamples) { this.codeExamples = codeExamples; }

    public String getImportantConcepts() { return importantConcepts; }
    public void setImportantConcepts(String importantConcepts) { this.importantConcepts = importantConcepts; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getPracticeQuestions() { return practiceQuestions; }
    public void setPracticeQuestions(String practiceQuestions) { this.practiceQuestions = practiceQuestions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

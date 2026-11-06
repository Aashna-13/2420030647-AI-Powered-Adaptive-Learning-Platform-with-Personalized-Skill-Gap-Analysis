package com.skillbridge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String learnerName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int targetScore;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Assessment() {}

    public Assessment(String learnerName, Topic topic, int score, int targetScore) {
        this.learnerName = learnerName;
        this.topic = topic;
        this.score = score;
        this.targetScore = targetScore;
        this.createdAt = LocalDateTime.now();
    }

    public Assessment(String learnerName, Topic topic, int score, int targetScore, User user) {
        this.learnerName = learnerName;
        this.topic = topic;
        this.score = score;
        this.targetScore = targetScore;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTargetScore() { return targetScore; }
    public void setTargetScore(int targetScore) { this.targetScore = targetScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


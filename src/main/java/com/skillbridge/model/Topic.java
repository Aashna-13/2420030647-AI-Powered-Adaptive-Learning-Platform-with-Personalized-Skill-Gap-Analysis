package com.skillbridge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "topics")
public class Topic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String careerRole;
    @Column(length = 1000)
    private String description;
    private int targetScore = 80;

    public Topic() {}
    public Topic(String name, String careerRole, String description, int targetScore) {
        this.name = name; this.careerRole = careerRole; this.description = description; this.targetScore = targetScore;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCareerRole() { return careerRole; }
    public String getDescription() { return description; }
    public int getTargetScore() { return targetScore; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCareerRole(String careerRole) { this.careerRole = careerRole; }
    public void setDescription(String description) { this.description = description; }
    public void setTargetScore(int targetScore) { this.targetScore = targetScore; }
}

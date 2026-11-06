package com.skillbridge.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "careers")
public class Career {

    @Id
    @Column(nullable = false, unique = true, length = 64)
    private String id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private int targetProficiency = 80;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "career_skills", joinColumns = @JoinColumn(name = "career_id"))
    @Column(name = "skill_name", nullable = false, length = 80)
    private List<String> requiredSkills = new ArrayList<>();

    @Column(length = 60)
    private String icon = "bi-briefcase";

    @Column(length = 30)
    private String badgeColor = "primary";

    public Career() {}

    public Career(String id, String title, String description, int targetProficiency, List<String> requiredSkills, String icon, String badgeColor) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetProficiency = targetProficiency;
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
        this.icon = icon;
        this.badgeColor = badgeColor;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTargetProficiency() { return targetProficiency; }
    public void setTargetProficiency(int targetProficiency) { this.targetProficiency = targetProficiency; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getBadgeColor() { return badgeColor; }
    public void setBadgeColor(String badgeColor) { this.badgeColor = badgeColor; }
}

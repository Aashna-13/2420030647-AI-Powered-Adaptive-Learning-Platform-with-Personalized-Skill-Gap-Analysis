package com.skillbridge.dto;

public record SkillScoreDto(
        String skillName,
        int correctAnswers,
        int totalQuestions,
        int score,
        int targetScore,
        int gap,
        String level
) {}

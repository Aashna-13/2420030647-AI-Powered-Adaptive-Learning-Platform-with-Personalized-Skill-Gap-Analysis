package com.skillbridge.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CareerAssessmentResultDto(
        Long assessmentId,
        String careerId,
        String careerTitle,
        String studentName,
        String studentEmail,
        int totalQuestions,
        int correctAnswers,
        double overallScore,
        int readinessPercentage,
        String strongestSkill,
        String weakestSkill,
        int targetProficiency,
        int totalSkillGap,
        List<SkillScoreDto> skillBreakdown,
        List<String> recommendations,
        String disclaimer,
        LocalDateTime createdAt
) {}

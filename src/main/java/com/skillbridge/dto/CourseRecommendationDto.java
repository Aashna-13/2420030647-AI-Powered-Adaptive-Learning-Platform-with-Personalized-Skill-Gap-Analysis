package com.skillbridge.dto;

public record CourseRecommendationDto(
        Long courseId,
        String courseSlug,
        String courseTitle,
        String description,
        String careerId,
        String careerTitle,
        String skillCategory,
        String difficulty,
        String duration,
        String icon,
        String badgeColor,
        int studentScore,
        int targetScore,
        int gap,
        String reason,
        boolean enrolled,
        String enrollmentStatus,
        int progressPercentage
) {}

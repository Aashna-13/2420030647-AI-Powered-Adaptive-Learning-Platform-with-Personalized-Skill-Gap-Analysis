package com.skillbridge.dto;

public record CourseDto(
        Long id,
        String slug,
        String title,
        String description,
        String careerId,
        String careerTitle,
        String skillCategory,
        String difficulty,
        String estimatedDuration,
        String icon,
        String badgeColor,
        long lessonCount,
        String enrollmentStatus,
        int progressPercentage,
        Long lastAccessedLessonId,
        Long firstLessonId
) {}

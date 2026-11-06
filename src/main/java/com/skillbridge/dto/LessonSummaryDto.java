package com.skillbridge.dto;

public record LessonSummaryDto(
        Long id,
        Long courseId,
        String title,
        int lessonOrder,
        int estimatedMinutes,
        boolean completed,
        boolean quizPassed
) {}

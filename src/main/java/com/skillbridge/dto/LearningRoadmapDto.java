package com.skillbridge.dto;

import java.util.List;
import java.util.Map;

public record LearningRoadmapDto(
        String careerId,
        String careerTitle,
        String studentName,
        int readinessScore,
        String weakestSkill,
        String strongestSkill,
        int totalIdentifiedGaps,
        List<CourseRecommendationDto> recommendedCourses,
        List<Map<String, Object>> prioritySkills,
        List<String> suggestedSequence,
        String totalEstimatedTime,
        int completedCoursesCount,
        int totalRecommendedCourses,
        boolean reassessmentRecommended,
        String reassessmentMessage,
        String generatedBy
) {}

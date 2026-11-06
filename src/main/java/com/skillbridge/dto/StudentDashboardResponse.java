package com.skillbridge.dto;

import java.util.List;
import java.util.Map;

public record StudentDashboardResponse(
        UserResponse student,
        int totalAssessments,
        double averageScore,
        String currentSkillLevel,
        int averageGap,
        String overallRecommendation,
        List<Map<String, Object>> assessmentHistory
) {}

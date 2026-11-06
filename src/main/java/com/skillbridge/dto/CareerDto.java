package com.skillbridge.dto;

import java.util.List;

public record CareerDto(
        String id,
        String title,
        String description,
        int targetProficiency,
        List<String> requiredSkills,
        String icon,
        String badgeColor,
        int totalQuestions
) {}

package com.skillbridge.dto;

import java.util.List;

public record CareerQuestionDto(
        Long id,
        String careerId,
        String skillCategory,
        String questionText,
        List<String> options,
        String difficulty
) {}

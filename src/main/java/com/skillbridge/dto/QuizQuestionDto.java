package com.skillbridge.dto;

import java.util.List;

public record QuizQuestionDto(
        Long id,
        Long lessonId,
        int questionOrder,
        String questionText,
        List<String> options
) {}

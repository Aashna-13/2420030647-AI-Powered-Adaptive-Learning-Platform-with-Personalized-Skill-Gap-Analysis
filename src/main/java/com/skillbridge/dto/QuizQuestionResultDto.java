package com.skillbridge.dto;

import java.util.List;

public record QuizQuestionResultDto(
        Long questionId,
        String questionText,
        List<String> options,
        int selectedOption,
        int correctOption,
        boolean correct,
        String explanation
) {}

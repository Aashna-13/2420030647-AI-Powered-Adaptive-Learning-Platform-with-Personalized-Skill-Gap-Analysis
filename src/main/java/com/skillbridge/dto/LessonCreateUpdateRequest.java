package com.skillbridge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LessonCreateUpdateRequest(
        @NotBlank @Size(max = 200) String title,
        int lessonOrder,
        int estimatedMinutes,
        String learningObjectives,
        String introduction,
        @NotBlank String content,
        String codeExamples,
        String importantConcepts,
        String summary,
        String practiceQuestions
) {}

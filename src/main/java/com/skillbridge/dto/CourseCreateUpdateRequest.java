package com.skillbridge.dto;

import com.skillbridge.model.QuestionDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseCreateUpdateRequest(
        @NotBlank @Size(max = 100) String slug,
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 1500) String description,
        @NotBlank @Size(max = 64) String careerId,
        @NotBlank @Size(max = 120) String careerTitle,
        @NotBlank @Size(max = 100) String skillCategory,
        QuestionDifficulty difficulty,
        @Size(max = 50) String estimatedDuration,
        @Size(max = 60) String icon,
        @Size(max = 30) String badgeColor,
        Boolean published
) {}

package com.skillbridge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuizQuestionCreateRequest(
        int questionOrder,
        @NotBlank @Size(max = 1000) String questionText,
        @NotBlank @Size(max = 500) String optionA,
        @NotBlank @Size(max = 500) String optionB,
        @NotBlank @Size(max = 500) String optionC,
        @NotBlank @Size(max = 500) String optionD,
        @Min(0) @Max(3) int correctOption,
        @NotBlank String explanation
) {}

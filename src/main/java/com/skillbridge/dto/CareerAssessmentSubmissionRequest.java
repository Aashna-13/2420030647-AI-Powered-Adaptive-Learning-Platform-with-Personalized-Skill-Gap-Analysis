package com.skillbridge.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record CareerAssessmentSubmissionRequest(
        @NotNull(message = "Answers map cannot be null")
        Map<Long, Integer> answers
) {}

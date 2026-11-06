package com.skillbridge.dto;

import java.util.Map;

public record QuizSubmissionRequest(
        Map<Long, Integer> answers
) {}

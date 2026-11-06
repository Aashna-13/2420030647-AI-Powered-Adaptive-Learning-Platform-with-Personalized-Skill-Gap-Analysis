package com.skillbridge.dto;

import java.util.List;

public record QuizSubmissionResultDto(
        Long lessonId,
        int totalQuestions,
        int correctAnswers,
        int scorePercentage,
        boolean passed,
        boolean lessonCompleted,
        int courseProgressPercentage,
        String feedbackMessage,
        List<QuizQuestionResultDto> questionResults
) {}

package com.skillbridge.dto;

public record LessonDetailDto(
        Long id,
        Long courseId,
        String courseSlug,
        String courseTitle,
        String title,
        int lessonOrder,
        int estimatedMinutes,
        String learningObjectives,
        String introduction,
        String content,
        String codeExamples,
        String importantConcepts,
        String summary,
        String practiceQuestions,
        boolean completed,
        boolean quizPassed,
        Long previousLessonId,
        Long nextLessonId,
        int totalLessonsInCourse,
        int courseProgressPercentage
) {}

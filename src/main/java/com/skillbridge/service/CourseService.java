package com.skillbridge.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.*;
import com.skillbridge.model.*;
import com.skillbridge.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonQuizQuestionRepository quizQuestionRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CareerAssessmentRepository careerAssessmentRepository;
    private final CareerRepository careerRepository;
    private final ObjectMapper objectMapper;

    public CourseService(
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            LessonQuizQuestionRepository quizQuestionRepository,
            CourseEnrollmentRepository enrollmentRepository,
            LessonProgressRepository lessonProgressRepository,
            QuizAttemptRepository quizAttemptRepository,
            CareerAssessmentRepository careerAssessmentRepository,
            CareerRepository careerRepository,
            ObjectMapper objectMapper
    ) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.careerAssessmentRepository = careerAssessmentRepository;
        this.careerRepository = careerRepository;
        this.objectMapper = objectMapper;
    }

    // =========================================================================
    // 1. COURSE CATALOG & BROWSING
    // =========================================================================

    public List<CourseDto> getCourses(User student, String careerId, String skill,
                                     QuestionDifficulty difficulty, String query) {
        String sanitizedCareer = (careerId != null && !careerId.isBlank() && !"ALL".equalsIgnoreCase(careerId)) ? careerId.trim() : null;
        String sanitizedSkill = (skill != null && !skill.isBlank() && !"ALL".equalsIgnoreCase(skill)) ? skill.trim() : null;
        String sanitizedQuery = (query != null && !query.isBlank()) ? query.trim() : null;

        List<Course> courses = courseRepository.searchCourses(sanitizedCareer, sanitizedSkill, difficulty, sanitizedQuery);
        Map<Long, CourseEnrollment> enrollmentMap = new HashMap<>();

        if (student != null) {
            List<CourseEnrollment> enrollments = enrollmentRepository.findByUserOrderByEnrolledAtDesc(student);
            for (CourseEnrollment e : enrollments) {
                enrollmentMap.put(e.getCourse().getId(), e);
            }
        }

        return courses.stream().map(course -> {
            long lessonCount = lessonRepository.countByCourseId(course.getId());
            CourseEnrollment enrollment = enrollmentMap.get(course.getId());
            String status = enrollment != null ? enrollment.getStatus().name() : "NOT_ENROLLED";
            int progress = enrollment != null ? enrollment.getProgressPercentage() : 0;
            Long lastLesson = enrollment != null ? enrollment.getLastAccessedLessonId() : null;

            List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
            Long firstLessonId = lessons.isEmpty() ? null : lessons.get(0).getId();

            return new CourseDto(
                    course.getId(),
                    course.getSlug(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getCareerId(),
                    course.getCareerTitle(),
                    course.getSkillCategory(),
                    course.getDifficulty().name(),
                    course.getEstimatedDuration(),
                    course.getIcon(),
                    course.getBadgeColor(),
                    lessonCount,
                    status,
                    progress,
                    lastLesson != null ? lastLesson : firstLessonId,
                    firstLessonId
            );
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getCourseDetails(String idOrSlug, User student) {
        Course course = findCourseByIdOrSlug(idOrSlug);
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());

        Set<Long> completedLessonIds = new HashSet<>();
        Set<Long> passedQuizLessonIds = new HashSet<>();
        CourseEnrollment enrollment = null;

        if (student != null) {
            enrollment = enrollmentRepository.findByUserAndCourse(student, course).orElse(null);
            List<LessonProgress> progresses = lessonProgressRepository.findByUserAndCourse(student, course);
            for (LessonProgress lp : progresses) {
                if (lp.isCompleted()) completedLessonIds.add(lp.getLesson().getId());
            }
            for (Lesson l : lessons) {
                quizAttemptRepository.findTopByUserAndLessonOrderByAttemptedAtDesc(student, l)
                        .ifPresent(qa -> {
                            if (qa.isPassed()) passedQuizLessonIds.add(l.getId());
                        });
            }
        }

        List<LessonSummaryDto> lessonSummaries = lessons.stream().map(l -> new LessonSummaryDto(
                l.getId(),
                course.getId(),
                l.getTitle(),
                l.getLessonOrder(),
                l.getEstimatedMinutes(),
                completedLessonIds.contains(l.getId()),
                passedQuizLessonIds.contains(l.getId())
        )).collect(Collectors.toList());

        Long firstLessonId = lessons.isEmpty() ? null : lessons.get(0).getId();
        Long resumeLessonId = null;
        if (enrollment != null) {
            resumeLessonId = enrollment.getLastAccessedLessonId();
            if (resumeLessonId == null || !completedLessonIds.contains(resumeLessonId)) {
                // Find first uncompleted lesson
                for (Lesson l : lessons) {
                    if (!completedLessonIds.contains(l.getId())) {
                        resumeLessonId = l.getId();
                        break;
                    }
                }
            }
        }
        if (resumeLessonId == null) resumeLessonId = firstLessonId;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("course", new CourseDto(
                course.getId(),
                course.getSlug(),
                course.getTitle(),
                course.getDescription(),
                course.getCareerId(),
                course.getCareerTitle(),
                course.getSkillCategory(),
                course.getDifficulty().name(),
                course.getEstimatedDuration(),
                course.getIcon(),
                course.getBadgeColor(),
                lessons.size(),
                enrollment != null ? enrollment.getStatus().name() : "NOT_ENROLLED",
                enrollment != null ? enrollment.getProgressPercentage() : 0,
                resumeLessonId,
                firstLessonId
        ));
        result.put("lessons", lessonSummaries);
        result.put("isEnrolled", enrollment != null);
        result.put("enrollmentDate", enrollment != null ? enrollment.getEnrolledAt().toString() : null);
        result.put("progressPercentage", enrollment != null ? enrollment.getProgressPercentage() : 0);
        result.put("resumeLessonId", resumeLessonId);
        return result;
    }

    // =========================================================================
    // 2. ENROLLMENT & MY COURSES
    // =========================================================================

    @Transactional
    public CourseDto enrollStudent(String idOrSlug, User student) {
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to enroll in courses.");
        }
        Course course = findCourseByIdOrSlug(idOrSlug);

        Optional<CourseEnrollment> existing = enrollmentRepository.findByUserAndCourse(student, course);
        CourseEnrollment enrollment;
        if (existing.isPresent()) {
            enrollment = existing.get();
        } else {
            enrollment = new CourseEnrollment(student, course);
            List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
            if (!lessons.isEmpty()) {
                enrollment.setLastAccessedLessonId(lessons.get(0).getId());
            }
            enrollment = enrollmentRepository.save(enrollment);
        }

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
        Long firstLessonId = lessons.isEmpty() ? null : lessons.get(0).getId();

        return new CourseDto(
                course.getId(),
                course.getSlug(),
                course.getTitle(),
                course.getDescription(),
                course.getCareerId(),
                course.getCareerTitle(),
                course.getSkillCategory(),
                course.getDifficulty().name(),
                course.getEstimatedDuration(),
                course.getIcon(),
                course.getBadgeColor(),
                lessons.size(),
                enrollment.getStatus().name(),
                enrollment.getProgressPercentage(),
                enrollment.getLastAccessedLessonId() != null ? enrollment.getLastAccessedLessonId() : firstLessonId,
                firstLessonId
        );
    }

    public List<CourseDto> getStudentEnrollments(User student) {
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        List<CourseEnrollment> enrollments = enrollmentRepository.findByUserOrderByEnrolledAtDesc(student);
        return enrollments.stream().map(enrollment -> {
            Course course = enrollment.getCourse();
            List<Lesson> lessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());
            Long firstLessonId = lessons.isEmpty() ? null : lessons.get(0).getId();

            // Find next incomplete lesson to resume
            List<LessonProgress> lpList = lessonProgressRepository.findByUserAndCourse(student, course);
            Set<Long> doneIds = lpList.stream().filter(LessonProgress::isCompleted).map(p -> p.getLesson().getId()).collect(Collectors.toSet());
            Long resumeId = null;
            for (Lesson l : lessons) {
                if (!doneIds.contains(l.getId())) {
                    resumeId = l.getId();
                    break;
                }
            }
            if (resumeId == null) resumeId = enrollment.getLastAccessedLessonId() != null ? enrollment.getLastAccessedLessonId() : firstLessonId;

            return new CourseDto(
                    course.getId(),
                    course.getSlug(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getCareerId(),
                    course.getCareerTitle(),
                    course.getSkillCategory(),
                    course.getDifficulty().name(),
                    course.getEstimatedDuration(),
                    course.getIcon(),
                    course.getBadgeColor(),
                    lessons.size(),
                    enrollment.getStatus().name(),
                    enrollment.getProgressPercentage(),
                    resumeId,
                    firstLessonId
            );
        }).collect(Collectors.toList());
    }

    // =========================================================================
    // 3. LESSON READING & PROGRESS
    // =========================================================================

    @Transactional
    public LessonDetailDto getLesson(Long lessonId, User student) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found: " + lessonId));
        Course course = lesson.getCourse();
        List<Lesson> allLessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(course.getId());

        boolean completed = false;
        boolean quizPassed = false;
        int courseProgress = 0;

        if (student != null) {
            // Auto-enroll if not enrolled
            CourseEnrollment enrollment = enrollmentRepository.findByUserAndCourse(student, course)
                    .orElseGet(() -> enrollmentRepository.save(new CourseEnrollment(student, course)));

            enrollment.setLastAccessedLessonId(lesson.getId());
            if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
                enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
            }
            enrollmentRepository.save(enrollment);

            LessonProgress progress = lessonProgressRepository.findByUserAndLesson(student, lesson).orElse(null);
            completed = progress != null && progress.isCompleted();

            QuizAttempt qa = quizAttemptRepository.findTopByUserAndLessonOrderByAttemptedAtDesc(student, lesson).orElse(null);
            quizPassed = qa != null && qa.isPassed();

            courseProgress = enrollment.getProgressPercentage();
        }

        // Determine previous and next lesson IDs
        Long prevId = null;
        Long nextId = null;
        for (int i = 0; i < allLessons.size(); i++) {
            if (allLessons.get(i).getId().equals(lesson.getId())) {
                if (i > 0) prevId = allLessons.get(i - 1).getId();
                if (i < allLessons.size() - 1) nextId = allLessons.get(i + 1).getId();
                break;
            }
        }

        return new LessonDetailDto(
                lesson.getId(),
                course.getId(),
                course.getSlug(),
                course.getTitle(),
                lesson.getTitle(),
                lesson.getLessonOrder(),
                lesson.getEstimatedMinutes(),
                lesson.getLearningObjectives(),
                lesson.getIntroduction(),
                lesson.getContent(),
                lesson.getCodeExamples(),
                lesson.getImportantConcepts(),
                lesson.getSummary(),
                lesson.getPracticeQuestions(),
                completed,
                quizPassed,
                prevId,
                nextId,
                allLessons.size(),
                courseProgress
        );
    }

    @Transactional
    public Map<String, Object> markLessonComplete(Long lessonId, User student) {
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to track progress.");
        }
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found: " + lessonId));
        Course course = lesson.getCourse();

        // Save lesson completion
        LessonProgress lp = lessonProgressRepository.findByUserAndLesson(student, lesson)
                .orElseGet(() -> new LessonProgress(student, lesson, course, false));
        lp.setCompleted(true);
        lessonProgressRepository.save(lp);

        // Update course enrollment progress
        CourseEnrollment enrollment = enrollmentRepository.findByUserAndCourse(student, course)
                .orElseGet(() -> new CourseEnrollment(student, course));

        long completedLessons = lessonProgressRepository.countByUserAndCourseAndCompletedTrue(student, course);
        long totalLessons = lessonRepository.countByCourseId(course.getId());
        int progress = totalLessons == 0 ? 100 : (int) Math.round(((double) completedLessons / totalLessons) * 100.0);

        enrollment.setProgressPercentage(progress);
        enrollment.setLastAccessedLessonId(lesson.getId());
        if (progress >= 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        } else {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        }
        enrollmentRepository.save(enrollment);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("lessonId", lesson.getId());
        response.put("lessonCompleted", true);
        response.put("courseId", course.getId());
        response.put("courseProgressPercentage", progress);
        response.put("enrollmentStatus", enrollment.getStatus().name());
        response.put("completedLessons", completedLessons);
        response.put("totalLessons", totalLessons);
        return response;
    }

    // =========================================================================
    // 4. LESSON QUIZZES
    // =========================================================================

    public List<QuizQuestionDto> getLessonQuiz(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found: " + lessonId));
        List<LessonQuizQuestion> questions = quizQuestionRepository.findByLessonIdOrderByQuestionOrderAsc(lesson.getId());

        return questions.stream().map(q -> new QuizQuestionDto(
                q.getId(),
                lesson.getId(),
                q.getQuestionOrder(),
                q.getQuestionText(),
                List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD())
        )).collect(Collectors.toList());
    }

    @Transactional
    public QuizSubmissionResultDto submitLessonQuiz(Long lessonId, User student, QuizSubmissionRequest request) {
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to submit quizzes.");
        }
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found: " + lessonId));
        List<LessonQuizQuestion> questions = quizQuestionRepository.findByLessonIdOrderByQuestionOrderAsc(lesson.getId());

        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No quiz configured for this lesson.");
        }
        if (request == null || request.answers() == null || request.answers().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz answers cannot be empty.");
        }

        Map<Long, Integer> submitted = request.answers();
        int correctCount = 0;
        List<QuizQuestionResultDto> results = new ArrayList<>();

        for (LessonQuizQuestion q : questions) {
            Integer selected = submitted.get(q.getId());
            if (selected == null || selected < 0 || selected > 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or missing answer for question ID: " + q.getId());
            }
            boolean isCorrect = selected == q.getCorrectOption();
            if (isCorrect) correctCount++;

            results.add(new QuizQuestionResultDto(
                    q.getId(),
                    q.getQuestionText(),
                    List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                    selected,
                    q.getCorrectOption(),
                    isCorrect,
                    q.getExplanation()
            ));
        }

        int scorePercentage = (int) Math.round(((double) correctCount / questions.size()) * 100.0);
        boolean passed = scorePercentage >= 70; // 70% passing threshold

        String answersJson;
        try {
            answersJson = objectMapper.writeValueAsString(submitted);
        } catch (Exception e) {
            answersJson = "{}";
        }

        QuizAttempt attempt = new QuizAttempt(
                student,
                lesson,
                scorePercentage,
                questions.size(),
                correctCount,
                passed,
                answersJson
        );
        quizAttemptRepository.save(attempt);

        // If passed, mark the lesson as completed!
        int courseProgress = 0;
        boolean lessonCompleted = false;
        if (passed) {
            Map<String, Object> progressResult = markLessonComplete(lessonId, student);
            courseProgress = (int) progressResult.get("courseProgressPercentage");
            lessonCompleted = true;
        } else {
            CourseEnrollment enrollment = enrollmentRepository.findByUserAndCourse(student, lesson.getCourse()).orElse(null);
            if (enrollment != null) courseProgress = enrollment.getProgressPercentage();
        }

        String message = passed
                ? "Excellent! You scored " + scorePercentage + "% and mastered this lesson's core concepts."
                : "You scored " + scorePercentage + "%. Review the explanations below and retry the quiz to cement your understanding.";

        return new QuizSubmissionResultDto(
                lesson.getId(),
                questions.size(),
                correctCount,
                scorePercentage,
                passed,
                lessonCompleted,
                courseProgress,
                message,
                results
        );
    }

    // =========================================================================
    // 5. PERSONALIZED RECOMMENDATIONS & ROADMAP
    // =========================================================================

    public List<CourseRecommendationDto> getPersonalizedRecommendations(User student) {
        if (student == null) {
            return getFallbackRecommendations();
        }

        List<CareerAssessment> assessments = careerAssessmentRepository.findByUserOrderByCreatedAtDesc(student);
        if (assessments.isEmpty()) {
            return getFallbackRecommendations();
        }

        CareerAssessment latest = assessments.get(0);
        List<SkillScoreDto> skills = parseSkills(latest);
        if (skills.isEmpty()) {
            return getFallbackRecommendations();
        }

        // Filter weak skills (gap > 0) and sort by largest gap descending
        List<SkillScoreDto> weakSkills = skills.stream()
                .filter(s -> s.gap() > 0)
                .sorted(Comparator.comparingInt(SkillScoreDto::gap).reversed())
                .collect(Collectors.toList());

        if (weakSkills.isEmpty()) {
            // Student met all targets in latest assessment! Recommend advanced/adjacent courses
            weakSkills = skills.stream()
                    .sorted(Comparator.comparingInt(SkillScoreDto::score))
                    .collect(Collectors.toList());
        }

        List<CourseRecommendationDto> recommendations = new ArrayList<>();
        Set<Long> addedCourseIds = new HashSet<>();

        // Map enrollments for quick lookup
        Map<Long, CourseEnrollment> enrollmentMap = enrollmentRepository.findByUserOrderByEnrolledAtDesc(student)
                .stream().collect(Collectors.toMap(e -> e.getCourse().getId(), e -> e, (a, b) -> a));

        for (SkillScoreDto skill : weakSkills) {
            // Find courses matching this skill in the career or across catalog
            List<Course> matching = findCoursesForSkillAndCareer(skill.skillName(), latest.getCareerId());
            for (Course course : matching) {
                if (addedCourseIds.contains(course.getId())) continue;

                CourseEnrollment enrollment = enrollmentMap.get(course.getId());
                boolean isEnrolled = enrollment != null;
                String status = isEnrolled ? enrollment.getStatus().name() : "NOT_ENROLLED";
                int progress = isEnrolled ? enrollment.getProgressPercentage() : 0;

                String reason = "You scored " + skill.score() + "% in " + skill.skillName() +
                        " (target " + skill.targetScore() + "%). We recommend " + course.getTitle() +
                        " because it directly addresses your skill gap of " + skill.gap() + " points.";

                recommendations.add(new CourseRecommendationDto(
                        course.getId(),
                        course.getSlug(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getCareerId(),
                        course.getCareerTitle(),
                        course.getSkillCategory(),
                        course.getDifficulty().name(),
                        course.getEstimatedDuration(),
                        course.getIcon(),
                        course.getBadgeColor(),
                        skill.score(),
                        skill.targetScore(),
                        skill.gap(),
                        reason,
                        isEnrolled,
                        status,
                        progress
                ));
                addedCourseIds.add(course.getId());
            }
        }

        // If fewer than 3 recommendations found, add general courses for this career
        if (recommendations.size() < 3) {
            List<Course> careerCourses = courseRepository.findByCareerIdIgnoreCaseAndPublishedTrue(latest.getCareerId());
            for (Course course : careerCourses) {
                if (addedCourseIds.contains(course.getId())) continue;
                CourseEnrollment enrollment = enrollmentMap.get(course.getId());
                boolean isEnrolled = enrollment != null;
                String status = isEnrolled ? enrollment.getStatus().name() : "NOT_ENROLLED";
                int progress = isEnrolled ? enrollment.getProgressPercentage() : 0;

                String reason = "Recommended to reinforce core competencies for your target career: " + latest.getCareerTitle() + ".";

                recommendations.add(new CourseRecommendationDto(
                        course.getId(),
                        course.getSlug(),
                        course.getTitle(),
                        course.getDescription(),
                        course.getCareerId(),
                        course.getCareerTitle(),
                        course.getSkillCategory(),
                        course.getDifficulty().name(),
                        course.getEstimatedDuration(),
                        course.getIcon(),
                        course.getBadgeColor(),
                        0,
                        80,
                        0,
                        reason,
                        isEnrolled,
                        status,
                        progress
                ));
                addedCourseIds.add(course.getId());
            }
        }

        return recommendations;
    }

    public LearningRoadmapDto getLearningRoadmap(User student) {
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required to view learning roadmap.");
        }

        List<CareerAssessment> assessments = careerAssessmentRepository.findByUserOrderByCreatedAtDesc(student);
        if (assessments.isEmpty()) {
            return new LearningRoadmapDto(
                    "none",
                    "No Career Assessed Yet",
                    student.getFullName(),
                    0,
                    "None",
                    "None",
                    0,
                    List.of(),
                    List.of(),
                    List.of(),
                    "0 hours",
                    0,
                    0,
                    false,
                    "Take a Career Assessment to generate your custom learning roadmap.",
                    "SkillBridge Adaptive Assessment Engine"
            );
        }

        CareerAssessment latest = assessments.get(0);
        List<SkillScoreDto> skills = parseSkills(latest);
        List<CourseRecommendationDto> recommendations = getPersonalizedRecommendations(student);

        List<Map<String, Object>> prioritySkills = new ArrayList<>();
        int totalGaps = 0;
        for (SkillScoreDto s : skills) {
            if (s.gap() > 0) {
                totalGaps += s.gap();
                prioritySkills.add(Map.of(
                        "skill", s.skillName(),
                        "score", s.score(),
                        "target", s.targetScore(),
                        "gap", s.gap(),
                        "level", s.level(),
                        "priority", s.gap() >= 30 ? "HIGH" : s.gap() >= 15 ? "MEDIUM" : "LOW"
                ));
            }
        }
        prioritySkills.sort((a, b) -> Integer.compare((int) b.get("gap"), (int) a.get("gap")));

        List<String> suggestedSequence = recommendations.stream()
                .map(r -> "Step " + (recommendations.indexOf(r) + 1) + ": " + r.courseTitle() + " (" + r.difficulty() + ", " + r.duration() + ")")
                .collect(Collectors.toList());

        long completedCourses = recommendations.stream().filter(r -> "COMPLETED".equals(r.enrollmentStatus())).count();

        // Calculate total estimated hours
        int totalHours = recommendations.size() * 3; // Approx 3 hrs per course
        String totalTime = totalHours + " hours";

        String reassessmentMessage = "Completing courses builds theoretical understanding and practical problem-solving. However, completing a course does NOT automatically grant skill mastery — retake the "
                + latest.getCareerTitle() + " Assessment to measure and verify your score improvement.";

        return new LearningRoadmapDto(
                latest.getCareerId(),
                latest.getCareerTitle(),
                student.getFullName(),
                latest.getReadinessPercentage(),
                latest.getWeakestSkill(),
                latest.getStrongestSkill(),
                totalGaps,
                recommendations,
                prioritySkills,
                suggestedSequence,
                totalTime,
                (int) completedCourses,
                recommendations.size(),
                true,
                reassessmentMessage,
                "SkillBridge Adaptive Gap Analysis Engine"
        );
    }

    private List<Course> findCoursesForSkillAndCareer(String skillName, String careerId) {
        String cleanSkill = skillName.trim();
        List<Course> list = courseRepository.findByCareerIdIgnoreCaseAndSkillCategoryIgnoreCaseAndPublishedTrue(careerId, cleanSkill);
        if (!list.isEmpty()) return list;

        // Try fuzzy or cross-catalog match
        List<Course> all = courseRepository.findByPublishedTrueOrderByTitleAsc();
        return all.stream().filter(c ->
                c.getSkillCategory().equalsIgnoreCase(cleanSkill) ||
                c.getTitle().toLowerCase().contains(cleanSkill.toLowerCase()) ||
                cleanSkill.toLowerCase().contains(c.getSkillCategory().toLowerCase()) ||
                (c.getCareerId().equalsIgnoreCase(careerId) && c.getTitle().toLowerCase().contains(cleanSkill.toLowerCase().split(" ")[0]))
        ).collect(Collectors.toList());
    }

    private List<CourseRecommendationDto> getFallbackRecommendations() {
        List<Course> courses = courseRepository.findByCareerIdIgnoreCaseAndPublishedTrue("java-developer");
        if (courses.isEmpty()) courses = courseRepository.findByPublishedTrueOrderByTitleAsc();

        return courses.stream().limit(5).map(c -> new CourseRecommendationDto(
                c.getId(),
                c.getSlug(),
                c.getTitle(),
                c.getDescription(),
                c.getCareerId(),
                c.getCareerTitle(),
                c.getSkillCategory(),
                c.getDifficulty().name(),
                c.getEstimatedDuration(),
                c.getIcon(),
                c.getBadgeColor(),
                0,
                80,
                0,
                "Essential foundational curriculum for software developers.",
                false,
                "NOT_ENROLLED",
                0
        )).collect(Collectors.toList());
    }

    private Course findCourseByIdOrSlug(String idOrSlug) {
        if (idOrSlug == null || idOrSlug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course ID or slug required.");
        }
        try {
            Long id = Long.parseLong(idOrSlug);
            return courseRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + id));
        } catch (NumberFormatException e) {
            return courseRepository.findBySlugIgnoreCase(idOrSlug)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with slug: " + idOrSlug));
        }
    }

    private List<SkillScoreDto> parseSkills(CareerAssessment assessment) {
        try {
            if (assessment.getSkillBreakdownJson() == null || assessment.getSkillBreakdownJson().isBlank()) return List.of();
            return objectMapper.readValue(assessment.getSkillBreakdownJson(), new TypeReference<List<SkillScoreDto>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    // =========================================================================
    // 6. ADMIN OPERATIONS
    // =========================================================================

    @Transactional
    public Course createCourse(CourseCreateUpdateRequest request) {
        if (courseRepository.findBySlugIgnoreCase(request.slug()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A course with slug '" + request.slug() + "' already exists.");
        }
        Course course = new Course(
                request.slug(),
                request.title(),
                request.description(),
                request.careerId(),
                request.careerTitle(),
                request.skillCategory(),
                request.difficulty() != null ? request.difficulty() : QuestionDifficulty.BEGINNER,
                request.estimatedDuration() != null ? request.estimatedDuration() : "3 hours",
                request.icon() != null ? request.icon() : "bi-journal-code",
                request.badgeColor() != null ? request.badgeColor() : "primary"
        );
        if (request.published() != null) course.setPublished(request.published());
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, CourseCreateUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseId));

        if (!course.getSlug().equalsIgnoreCase(request.slug()) &&
                courseRepository.findBySlugIgnoreCase(request.slug()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A course with slug '" + request.slug() + "' already exists.");
        }

        course.setSlug(request.slug());
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setCareerId(request.careerId());
        course.setCareerTitle(request.careerTitle());
        course.setSkillCategory(request.skillCategory());
        if (request.difficulty() != null) course.setDifficulty(request.difficulty());
        if (request.estimatedDuration() != null) course.setEstimatedDuration(request.estimatedDuration());
        if (request.icon() != null) course.setIcon(request.icon());
        if (request.badgeColor() != null) course.setBadgeColor(request.badgeColor());
        if (request.published() != null) course.setPublished(request.published());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseId));
        courseRepository.delete(course);
    }

    @Transactional
    public Course togglePublish(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseId));
        course.setPublished(!course.isPublished());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Transactional
    public Lesson createLesson(Long courseId, LessonCreateUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseId));

        int order = request.lessonOrder() > 0 ? request.lessonOrder() : (int) lessonRepository.countByCourseId(course.getId()) + 1;

        Lesson lesson = new Lesson(
                course,
                request.title(),
                order,
                request.estimatedMinutes() > 0 ? request.estimatedMinutes() : 20,
                request.learningObjectives(),
                request.introduction(),
                request.content(),
                request.codeExamples(),
                request.importantConcepts(),
                request.summary(),
                request.practiceQuestions()
        );
        return lessonRepository.save(lesson);
    }

    @Transactional
    public Lesson updateLesson(Long lessonId, LessonCreateUpdateRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found: " + lessonId));

        lesson.setTitle(request.title());
        if (request.lessonOrder() > 0) lesson.setLessonOrder(request.lessonOrder());
        if (request.estimatedMinutes() > 0) lesson.setEstimatedMinutes(request.estimatedMinutes());
        lesson.setLearningObjectives(request.learningObjectives());
        lesson.setIntroduction(request.introduction());
        lesson.setContent(request.content());
        lesson.setCodeExamples(request.codeExamples());
        lesson.setImportantConcepts(request.importantConcepts());
        lesson.setSummary(request.summary());
        lesson.setPracticeQuestions(request.practiceQuestions());

        return lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found: " + lessonId));
        quizQuestionRepository.deleteByLessonId(lesson.getId());
        lessonRepository.delete(lesson);
    }

    @Transactional
    public List<LessonQuizQuestion> setLessonQuizQuestions(Long lessonId, List<QuizQuestionCreateRequest> requests) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found: " + lessonId));

        quizQuestionRepository.deleteByLessonId(lesson.getId());

        List<LessonQuizQuestion> list = new ArrayList<>();
        int order = 1;
        for (QuizQuestionCreateRequest req : requests) {
            list.add(new LessonQuizQuestion(
                    lesson,
                    req.questionOrder() > 0 ? req.questionOrder() : order++,
                    req.questionText(),
                    req.optionA(),
                    req.optionB(),
                    req.optionC(),
                    req.optionD(),
                    req.correctOption(),
                    req.explanation()
            ));
        }
        return quizQuestionRepository.saveAll(list);
    }
}

package com.skillbridge.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.dto.*;
import com.skillbridge.model.Career;
import com.skillbridge.model.CareerAssessment;
import com.skillbridge.model.CareerQuestion;
import com.skillbridge.model.QuestionDifficulty;
import com.skillbridge.model.User;
import com.skillbridge.repository.CareerAssessmentRepository;
import com.skillbridge.repository.CareerQuestionRepository;
import com.skillbridge.repository.CareerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CareerAssessmentService {

    public static final String READINESS_DISCLAIMER =
            "The career readiness score is an estimate based on your assessment results and is designed for learning and skill-gap identification, not as a guaranteed measure of employability.";

    private final CareerRepository careerRepository;
    private final CareerQuestionRepository questionRepository;
    private final CareerAssessmentRepository assessmentRepository;
    private final ObjectMapper objectMapper;

    public CareerAssessmentService(
            CareerRepository careerRepository,
            CareerQuestionRepository questionRepository,
            CareerAssessmentRepository assessmentRepository,
            ObjectMapper objectMapper
    ) {
        this.careerRepository = careerRepository;
        this.questionRepository = questionRepository;
        this.assessmentRepository = assessmentRepository;
        this.objectMapper = objectMapper;
    }

    public List<CareerDto> getAllCareers() {
        return careerRepository.findAll().stream()
                .map(this::toCareerDto)
                .collect(Collectors.toList());
    }

    public CareerDto getCareerById(String careerId) {
        Career career = careerRepository.findByIdIgnoreCase(careerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Career not found: " + careerId));
        return toCareerDto(career);
    }

    private CareerDto toCareerDto(Career career) {
        int totalQuestions = (int) questionRepository.countByCareerId(career.getId());
        return new CareerDto(
                career.getId(),
                career.getTitle(),
                career.getDescription(),
                career.getTargetProficiency(),
                career.getRequiredSkills(),
                career.getIcon(),
                career.getBadgeColor(),
                totalQuestions
        );
    }

    public List<CareerQuestionDto> getQuestionsForCareer(String careerId) {
        Career career = careerRepository.findByIdIgnoreCase(careerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Career not found: " + careerId));

        List<CareerQuestion> questions = questionRepository.findByCareerIdOrderByIdAsc(career.getId());
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No assessment questions found for career: " + careerId);
        }

        // Map to safe DTO that EXCLUDES correctOption
        return questions.stream()
                .map(q -> new CareerQuestionDto(
                        q.getId(),
                        q.getCareerId(),
                        q.getSkillCategory(),
                        q.getQuestionText(),
                        List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                        q.getDifficulty().name()
                ))
                .collect(Collectors.toList());
    }

    /** Selects a question mix using the student's most recent readiness for this career. */
    public List<CareerQuestionDto> getAdaptiveQuestions(String careerId, User student, CareerAssessmentRepository assessmentRepository) {
        Career career = careerRepository.findByIdIgnoreCase(careerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Career not found: " + careerId));
        List<CareerQuestion> all = questionRepository.findByCareerIdOrderByIdAsc(career.getId());
        if (all.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No assessment questions found for career: " + careerId);
        int readiness = student == null ? 0 : assessmentRepository.findByUserOrderByCreatedAtDesc(student).stream()
                .filter(a -> a.getCareerId().equalsIgnoreCase(careerId)).findFirst()
                .map(CareerAssessment::getReadinessPercentage).orElse(0);
        QuestionDifficulty preferred = readiness < 50 ? QuestionDifficulty.BEGINNER
                : readiness < 75 ? QuestionDifficulty.INTERMEDIATE : QuestionDifficulty.ADVANCED;
        // Keep the full assessment (so completion validation remains strict), but present the
        // student's current level first and progressively increase/decrease challenge.
        List<CareerQuestion> selected = new ArrayList<>(all);
        selected.sort(Comparator.comparingInt(q -> difficultyRank(q.getDifficulty(), preferred)));
        return selected.stream().map(q -> new CareerQuestionDto(q.getId(), q.getCareerId(), q.getSkillCategory(),
                q.getQuestionText(), List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()), q.getDifficulty().name()))
                .collect(Collectors.toList());
    }

    private int difficultyRank(QuestionDifficulty actual, QuestionDifficulty preferred) {
        if (actual == preferred) return 0;
        if (preferred == QuestionDifficulty.BEGINNER) return actual == QuestionDifficulty.INTERMEDIATE ? 1 : 2;
        if (preferred == QuestionDifficulty.INTERMEDIATE) return actual == QuestionDifficulty.BEGINNER ? 1 : 2;
        return actual == QuestionDifficulty.INTERMEDIATE ? 1 : 2;
    }

    @Transactional
    public CareerAssessmentResultDto submitAssessment(String careerId, User student, CareerAssessmentSubmissionRequest request) {
        Career career = careerRepository.findByIdIgnoreCase(careerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Career not found: " + careerId));

        if (request == null || request.answers() == null || request.answers().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assessment answers cannot be empty");
        }

        List<CareerQuestion> questions = questionRepository.findByCareerIdOrderByIdAsc(career.getId());
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No questions configured for this career");
        }

        Map<Long, Integer> submittedAnswers = request.answers();
        List<Long> missingQuestionIds = new ArrayList<>();
        Set<Long> validIds = questions.stream().map(CareerQuestion::getId).collect(Collectors.toSet());
        for (Long submittedId : submittedAnswers.keySet()) {
            if (!validIds.contains(submittedId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submission contains question IDs that do not belong to this career.");
            }
        }
        for (CareerQuestion q : questions) {
            Integer selected = submittedAnswers.get(q.getId());
            if (selected == null) missingQuestionIds.add(q.getId());
            else if (selected < 0 || selected > 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid option selected for question ID " + q.getId() + ". Must be 0, 1, 2, or 3.");
            }
        }
        if (!missingQuestionIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "All " + questions.size() + " questions must be answered before final submission. " +
                            missingQuestionIds.size() + " unanswered question(s).");
        }

        // Automatic Scoring
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        // Group by skill category
        Map<String, List<CareerQuestion>> skillGroup = questions.stream()
                .collect(Collectors.groupingBy(CareerQuestion::getSkillCategory, LinkedHashMap::new, Collectors.toList()));

        List<SkillScoreDto> skillBreakdown = new ArrayList<>();
        int targetProficiency = career.getTargetProficiency();
        int totalSkillGap = 0;

        for (Map.Entry<String, List<CareerQuestion>> entry : skillGroup.entrySet()) {
            String skillName = entry.getKey();
            List<CareerQuestion> skillQuestions = entry.getValue();

            int skillTotal = skillQuestions.size();
            int skillCorrect = 0;

            for (CareerQuestion q : skillQuestions) {
                int selected = submittedAnswers.get(q.getId());
                if (selected == q.getCorrectOption()) {
                    skillCorrect++;
                    correctAnswers++;
                }
            }

            int skillScore = (int) Math.round(((double) skillCorrect / skillTotal) * 100.0);
            int skillGap = Math.max(0, targetProficiency - skillScore);
            totalSkillGap += skillGap;
            String level = categorizeSkillLevel(skillScore);

            skillBreakdown.add(new SkillScoreDto(
                    skillName,
                    skillCorrect,
                    skillTotal,
                    skillScore,
                    targetProficiency,
                    skillGap,
                    level
            ));
        }

        // Overall score formula: (correctAnswers / totalQuestions) * 100
        double overallScore = Math.round(((double) correctAnswers / totalQuestions) * 1000.0) / 10.0;
        int readinessPercentage = (int) Math.round(overallScore);

        // Identify Strongest and Weakest skill
        SkillScoreDto strongest = skillBreakdown.stream()
                .max(Comparator.comparingInt(SkillScoreDto::score))
                .orElse(null);

        SkillScoreDto weakest = skillBreakdown.stream()
                .min(Comparator.comparingInt(SkillScoreDto::score))
                .orElse(null);

        String strongestSkill = strongest != null ? strongest.skillName() + " (" + strongest.score() + "%)" : "N/A";
        String weakestSkill = weakest != null ? weakest.skillName() + " (" + weakest.score() + "%)" : "N/A";

        // Generate tailored recommendations
        List<String> recommendations = generateRecommendations(skillBreakdown, targetProficiency);

        // Student identity
        String studentName = student != null ? student.getFullName() : "Demo Student";
        String studentEmail = student != null ? student.getEmail() : "student@skillbridge.ai";

        // Serialize breakdown and recommendations to JSON for persistence
        String breakdownJson;
        String recommendationsJson;
        try {
            breakdownJson = objectMapper.writeValueAsString(skillBreakdown);
            recommendationsJson = objectMapper.writeValueAsString(recommendations);
        } catch (Exception e) {
            breakdownJson = "[]";
            recommendationsJson = "[]";
        }

        CareerAssessment assessment = new CareerAssessment();
        assessment.setUser(student);
        assessment.setStudentName(studentName);
        assessment.setStudentEmail(studentEmail);
        assessment.setCareerId(career.getId());
        assessment.setCareerTitle(career.getTitle());
        assessment.setTotalQuestions(totalQuestions);
        assessment.setCorrectAnswers(correctAnswers);
        assessment.setOverallScore(overallScore);
        assessment.setReadinessPercentage(readinessPercentage);
        assessment.setStrongestSkill(strongestSkill);
        assessment.setWeakestSkill(weakestSkill);
        assessment.setSkillBreakdownJson(breakdownJson);
        assessment.setRecommendationsJson(recommendationsJson);

        CareerAssessment saved = assessmentRepository.save(assessment);

        return new CareerAssessmentResultDto(
                saved.getId(),
                career.getId(),
                career.getTitle(),
                studentName,
                studentEmail,
                totalQuestions,
                correctAnswers,
                overallScore,
                readinessPercentage,
                strongestSkill,
                weakestSkill,
                targetProficiency,
                totalSkillGap,
                skillBreakdown,
                recommendations,
                READINESS_DISCLAIMER,
                saved.getCreatedAt()
        );
    }

    public List<CareerAssessmentResultDto> getAssessmentsForUser(User student) {
        List<CareerAssessment> assessments;
        if (student != null) {
            assessments = assessmentRepository.findByUserOrderByCreatedAtDesc(student);
            if (assessments.isEmpty()) {
                assessments = assessmentRepository.findByStudentEmailIgnoreCaseOrderByCreatedAtDesc(student.getEmail());
            }
        } else {
            assessments = Collections.emptyList();
        }

        return assessments.stream()
                .map(this::toResultDto)
                .collect(Collectors.toList());
    }

    public CareerAssessmentResultDto getAssessmentById(Long id, User currentUser) {
        CareerAssessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Career assessment not found: " + id));

        // Ownership check: if current user is student, ensure it belongs to them
        if (currentUser != null && !currentUser.getRole().name().equals("ADMIN")) {
            boolean isOwner = (assessment.getUser() != null && assessment.getUser().getId().equals(currentUser.getId()))
                    || (assessment.getStudentEmail() != null && assessment.getStudentEmail().equalsIgnoreCase(currentUser.getEmail()));
            if (!isOwner) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You can only view your own assessment results");
            }
        }

        return toResultDto(assessment);
    }

    private CareerAssessmentResultDto toResultDto(CareerAssessment a) {
        List<SkillScoreDto> breakdown = Collections.emptyList();
        List<String> recommendations = Collections.emptyList();
        try {
            if (a.getSkillBreakdownJson() != null && !a.getSkillBreakdownJson().isBlank()) {
                breakdown = objectMapper.readValue(a.getSkillBreakdownJson(), new TypeReference<List<SkillScoreDto>>() {});
            }
            if (a.getRecommendationsJson() != null && !a.getRecommendationsJson().isBlank()) {
                recommendations = objectMapper.readValue(a.getRecommendationsJson(), new TypeReference<List<String>>() {});
            }
        } catch (Exception ignored) {}

        int target = 80;
        int totalGap = breakdown.stream().mapToInt(SkillScoreDto::gap).sum();

        return new CareerAssessmentResultDto(
                a.getId(),
                a.getCareerId(),
                a.getCareerTitle(),
                a.getStudentName(),
                a.getStudentEmail(),
                a.getTotalQuestions(),
                a.getCorrectAnswers(),
                a.getOverallScore(),
                a.getReadinessPercentage(),
                a.getStrongestSkill(),
                a.getWeakestSkill(),
                target,
                totalGap,
                breakdown,
                recommendations,
                READINESS_DISCLAIMER,
                a.getCreatedAt()
        );
    }

    public static String categorizeSkillLevel(int score) {
        if (score >= 80) return "Strong";
        if (score >= 60) return "Intermediate";
        if (score >= 40) return "Developing";
        return "Needs Improvement";
    }

    private List<String> generateRecommendations(List<SkillScoreDto> skills, int target) {
        List<String> recs = new ArrayList<>();
        // Sort by largest gap descending
        List<SkillScoreDto> sorted = skills.stream()
                .sorted(Comparator.comparingInt(SkillScoreDto::gap).reversed())
                .toList();

        for (SkillScoreDto s : sorted) {
            if (s.gap() > 0) {
                if (s.score() < 40) {
                    recs.add("Critical Focus: " + s.skillName() + " (" + s.score() + "%, Gap: " + s.gap() +
                            " pts). Revisit fundamentals and introductory practice sets before moving to applied projects.");
                } else if (s.score() < 60) {
                    recs.add("Developing Area: " + s.skillName() + " (" + s.score() + "%, Gap: " + s.gap() +
                            " pts). Solidify core patterns and practice intermediate problem scenarios.");
                } else {
                    recs.add("Refine: " + s.skillName() + " (" + s.score() + "%, Gap: " + s.gap() +
                            " pts). Address edge cases and performance optimization to reach benchmark.");
                }
            } else {
                recs.add("Strength: " + s.skillName() + " (" + s.score() + "%, Target Met). Explore advanced real-world projects and system architecture.");
            }
        }
        return recs;
    }
}

package com.skillbridge.service;

import com.skillbridge.model.Assessment;
import com.skillbridge.model.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SkillGapServiceTest {

    private SkillGapService skillGapService;
    private Topic sampleTopic;

    @BeforeEach
    void setUp() {
        skillGapService = new SkillGapService();
        sampleTopic = new Topic("Java Programming", "Java Developer", "Core Java, OOP, collections", 80);
    }

    @Test
    @DisplayName("Selected score 55 with target 80 gives displayed score 55, target 80, gap 25 and level Needs improvement")
    void testAssessmentScore55Target80() {
        Assessment assessment = new Assessment("Demo Student", sampleTopic, 55, sampleTopic.getTargetScore());
        assessment.setId(10L);

        Map<String, Object> result = skillGapService.analyze(assessment);

        assertEquals(10L, result.get("assessmentId"));
        assertEquals("Java Programming", result.get("topic"));
        assertEquals(55, result.get("score"), "Displayed score must match the exact submitted score (55)");
        assertEquals(80, result.get("targetScore"), "Target score must match topic target (80)");
        assertEquals(25, result.get("gap"), "Skill gap must equal target (80) - score (55) = 25");
        assertEquals("Needs improvement", result.get("level"), "Score 55 should be classified as Needs improvement");
        assertTrue(result.get("recommendation").toString().contains("25-point gap"));
    }

    @Test
    @DisplayName("Score meeting or exceeding target gives gap 0 and Strong level")
    void testAssessmentScoreMeetingTarget() {
        Assessment assessment = new Assessment("Alice", sampleTopic, 85, sampleTopic.getTargetScore());
        assessment.setId(11L);

        Map<String, Object> result = skillGapService.analyze(assessment);

        assertEquals(85, result.get("score"));
        assertEquals(80, result.get("targetScore"));
        assertEquals(0, result.get("gap"), "Gap cannot be negative; must be 0 when target is exceeded");
        assertEquals("Strong", result.get("level"));
    }

    @Test
    @DisplayName("Score 65 gives gap 15 and Intermediate level")
    void testAssessmentScore65() {
        Assessment assessment = new Assessment("Bob", sampleTopic, 65, sampleTopic.getTargetScore());
        assessment.setId(12L);

        Map<String, Object> result = skillGapService.analyze(assessment);

        assertEquals(65, result.get("score"));
        assertEquals(80, result.get("targetScore"));
        assertEquals(15, result.get("gap"));
        assertEquals("Intermediate", result.get("level"));
    }

    @Test
    @DisplayName("Score 30 gives gap 50 and Beginner level")
    void testAssessmentScore30() {
        Assessment assessment = new Assessment("Charlie", sampleTopic, 30, sampleTopic.getTargetScore());
        assessment.setId(13L);

        Map<String, Object> result = skillGapService.analyze(assessment);

        assertEquals(30, result.get("score"));
        assertEquals(80, result.get("targetScore"));
        assertEquals(50, result.get("gap"));
        assertEquals("Beginner", result.get("level"));
    }

    @Test
    @DisplayName("Mathematical consistency check across various scores")
    void testMathematicalConsistency() {
        int[] scores = {0, 20, 40, 55, 60, 75, 80, 95, 100};
        int target = 80;

        for (int s : scores) {
            Assessment a = new Assessment("Test User", sampleTopic, s, target);
            Map<String, Object> res = skillGapService.analyze(a);

            int outScore = (int) res.get("score");
            int outTarget = (int) res.get("targetScore");
            int outGap = (int) res.get("gap");

            assertEquals(s, outScore);
            assertEquals(target, outTarget);
            assertEquals(Math.max(0, outTarget - outScore), outGap);
        }
    }
}

package com.skillbridge.service;

import com.skillbridge.model.Assessment;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SkillGapService {

    public Map<String, Object> analyze(Assessment assessment) {
        int score = assessment.getScore();
        int target = assessment.getTargetScore();
        int gap = Math.max(0, target - score);
        String topicName = assessment.getTopic() != null ? assessment.getTopic().getName() : "Topic";
        String level = determineLevel(score);
        String recommendation = determineRecommendation(score, target, topicName);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("assessmentId", assessment.getId());
        result.put("topic", topicName);
        result.put("score", score);
        result.put("targetScore", target);
        result.put("gap", gap);
        result.put("level", level);
        result.put("recommendation", recommendation);
        return result;
    }

    public String determineLevel(int score) {
        if (score >= 80) return "Strong";
        if (score >= 60) return "Intermediate";
        if (score >= 40) return "Needs improvement";
        return "Beginner";
    }

    public String determineRecommendation(int score, int target, String topicName) {
        if (score >= target) {
            return "You reached the target of " + target + "%. Try an advanced practice assessment or project.";
        }
        return "Focus on " + topicName + " fundamentals to close the " + (target - score) + "-point gap, then reassess.";
    }
}


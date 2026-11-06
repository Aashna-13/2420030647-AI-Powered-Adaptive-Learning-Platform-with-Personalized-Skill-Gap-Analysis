package com.skillbridge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/** Optional server-side Gemini integration. Never expose the provider key to browser code. */
@Service
public class AiLearningService {
    private final String apiKey;
    private final String model;
    private final RestClient restClient;

    public AiLearningService(
            @Value("${ai.gemini.api-key:}") String apiKey,
            @Value("${ai.gemini.model:gemini-2.5-flash}") String model,
            RestClient.Builder builder
    ) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
        this.restClient = builder.build();
    }

    public String createStudyAdvice(String career, List<Map<String, Object>> gapTasks) {
        if (apiKey.isBlank() || gapTasks == null || gapTasks.isEmpty()) return null;
        String prompt = "You are a concise learning coach for a college student. Based only on this career and skill-gap data, " +
                "write a supportive 4-6 sentence study strategy with priorities, realistic sequencing, and reassessment advice. " +
                "Do not invent scores, guarantee employability, or claim resources were completed. Career: " + career + ". Gaps: " + gapTasks;
        try {
            Map<String, Object> request = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
            Map response = restClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}", model, apiKey)
                    .body(request)
                    .retrieve()
                    .body(Map.class);
            if (response == null) return null;
            List candidates = (List) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) return null;
            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            if (content == null) return null;
            List parts = (List) content.get("parts");
            if (parts == null || parts.isEmpty()) return null;
            Object text = ((Map) parts.get(0)).get("text");
            return text instanceof String s && !s.isBlank() ? s.trim() : null;
        } catch (Exception ignored) {
            // API quota, network, or model errors should not break core learning features.
            return null;
        }
    }
}

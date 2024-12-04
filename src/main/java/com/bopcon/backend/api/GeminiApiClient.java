package com.bopcon.backend.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GeminiApiClient {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public JsonNode generatePredictedSetlist(String prompt) {
        try {
            // 요청 데이터 생성
            ObjectMapper mapper = new ObjectMapper();
            JsonNode requestJson = mapper.createObjectNode()
                    .set("contents", mapper.createArrayNode()
                            .add(mapper.createObjectNode()
                                    .set("parts", mapper.createArrayNode()
                                            .add(mapper.createObjectNode().put("text", prompt)))));

            // WebClient 요청
            String response = webClientBuilder.build()
                    .post()
                    .uri(apiUrl)
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", apiKey)
                    .bodyValue(mapper.writeValueAsString(requestJson))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println(response);
            return mapper.readTree(response); // 응답 JSON 파싱
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }
}
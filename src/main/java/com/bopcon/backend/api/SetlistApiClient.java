package com.bopcon.backend.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SetlistApiClient {

    private final WebClient webClient;
    private final String apiKey;

    @Autowired
    public SetlistApiClient(WebClient.Builder builder, @Value("${setlist.api.key}") String apiKey) {
        this.webClient = builder
                .baseUrl("https://api.setlist.fm/rest/1.0")
                .build();

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API Key is not set in application properties or environment variables");
        }
        this.apiKey = apiKey;
    }

    public JsonNode fetchSetlists(String mbid) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/artist/{mbid}/setlists")
                            .build(mbid))
                    .header("x-api-key", apiKey)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch setlists from Setlist.fm API");
        }
    }
}

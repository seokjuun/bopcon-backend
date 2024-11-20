package com.bopcon.backend.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SetlistApiClient {

    private final WebClient webClient;
    private final String apiKey;

    @Autowired
    public SetlistApiClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.setlist.fm/rest/1.0")
                .build();

        // .env 파일에서 API 키 읽기
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("SETLIST_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("API Key is not set in .env file");
        }
    }

    public JsonNode fetchSetlists(String mbid) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/artist/{mbid}/setlists")
                            .build(mbid))
                    .header("x-api-key", apiKey) // .env에서 가져온 API Key
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

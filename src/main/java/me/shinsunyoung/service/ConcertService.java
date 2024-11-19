package me.shinsunyoung.service;

import me.shinsunyoung.dto.SetlistDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConcertService {

    private final RestTemplate restTemplate = new RestTemplate();

    public SetlistDTO fetchConcertData() {
        String apiUrl = "https://api.setlist.fm/rest/1.0/artist/b2f2216a-d7a9-4ce0-8b8f-f494d9a8c196/setlists"; // API URL
        String apiKey = "mgdCvDsVafcCrHODYgS1eHGoFOAI9o6VLVdn"; // API 키

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<SetlistDTO> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    SetlistDTO.class
            );

            return response.getBody(); // 성공적으로 데이터를 가져온 경우 반환
        } catch (Exception e) {
            System.err.println("API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return new SetlistDTO(); // 빈 객체 반환
        }
    }
}

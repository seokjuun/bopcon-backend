package me.shinsunyoung.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.shinsunyoung.entity.Concert;
import me.shinsunyoung.entity.Song;
import me.shinsunyoung.repository.ConcertRepository;
import me.shinsunyoung.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetlistService {

    private final RestTemplate restTemplate;
    private final ConcertRepository concertRepository;
    private final SongRepository songRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public SetlistService(RestTemplate restTemplate, ConcertRepository concertRepository, SongRepository songRepository) {
        this.restTemplate = restTemplate;
        this.concertRepository = concertRepository;
        this.songRepository = songRepository;
    }

    // API에서 데이터를 가져와 Concert 및 Song 테이블에 저장하는 메서드
    public void fetchAndSaveSetlists() {
        String apiUrl = "https://api.setlist.fm/rest/1.0/artist/b2f2216a-d7a9-4ce0-8b8f-f494d9a8c196/setlists";
        String apiKey = "mgdCvDsVafcCrHODYgS1eHGoFOAI9o6VLVdn";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            List<Concert> concerts = new ArrayList<>();

            for (JsonNode setlistNode : root.path("setlist")) {
                String date = setlistNode.path("eventDate").asText();
                String venue = setlistNode.path("venue").path("name").asText();
                String city = setlistNode.path("venue").path("city").path("name").asText();
                String artistName = setlistNode.path("artist").path("name").asText(); // 아티스트 이름 가져오기

                // Setlist 정보 수집
                List<String> songs = new ArrayList<>();
                for (JsonNode songNode : setlistNode.path("sets").path("set").get(0).path("song")) {
                    String songTitle = songNode.path("name").asText();

                    // "Ment"가 포함된 노래 제목은 저장하지 않음
                    if (!songTitle.equalsIgnoreCase("Ment") && !songTitle.toLowerCase().contains("ment")) {
                        boolean songExists = songRepository.existsByArtistNameAndTitle(artistName, songTitle);
                        if (!songExists) {
                            Song song = new Song(artistName, songTitle, null); // ytLink는 초기값을 null로 저장
                            songRepository.save(song);
                        }
                        songs.add(songTitle);
                    }
                }
                String setlist = String.join(", ", songs);

                // Concert 데이터 중복 체크 후 저장
                boolean concertExists = concertRepository.existsByDateAndVenueAndCity(date, venue, city);
                if (!concertExists) {
                    concerts.add(new Concert(date, venue, city, setlist, artistName));
                }
            }

            concertRepository.saveAll(concerts); // 중복되지 않는 Concert 데이터만 저장
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 데이터베이스에서 저장된 Concert 데이터를 조회하여 반환하는 메서드
    public List<Concert> getSetlist() {
        return concertRepository.findAll();
    }
}

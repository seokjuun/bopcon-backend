package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.SetlistDTO;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.NewConcertRepository;
import com.bopcon.backend.repository.SongRepository;
import com.bopcon.backend.service.ConcertSetlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/setlists")
public class SetlistApiController {

    private final ConcertSetlistService concertSetlistService;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final NewConcertRepository newConcertRepository;

    @Autowired
    public SetlistApiController(
            ConcertSetlistService concertSetlistService,
            SongRepository songRepository,
            ArtistRepository artistRepository,
            NewConcertRepository newConcertRepository
    ) {
        this.concertSetlistService = concertSetlistService;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.newConcertRepository = newConcertRepository;
    }

    /**
     * 특정 아티스트의 PastConcert 셋리스트 조회
     *
     * @param artistId 아티스트 ID
     * @param type     조회 유형 (현재는 "past"만 허용)
     * @return 아티스트의 PastConcert와 셋리스트
     */
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<?> getSetlistsByArtistId(
            @PathVariable Long artistId,
            @RequestParam(required = false, defaultValue = "past") String type) {
        if (!type.equalsIgnoreCase("past")) {
            return ResponseEntity.badRequest().body("Invalid type. Only 'past' is supported.");
        }

        try {
            List<PastConcertDTO> result = concertSetlistService.getSetlistsByArtistId(artistId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving past setlists."));
        }
    }

    /**
     * 특정 PastConcert의 셋리스트 조회
     *
     * @param pastConcertId 과거 콘서트 ID
     * @return PastConcert에 포함된 셋리스트
     */
    @GetMapping("/past-concert/{pastConcertId}")
    public ResponseEntity<?> getSetlistByPastConcertId(@PathVariable Long pastConcertId) {
        try {
            List<SetlistDTO> setlist = concertSetlistService.getSetlistByPastConcertId(
                    concertSetlistService.findPastConcertById(pastConcertId)
            );
            return ResponseEntity.ok(setlist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving the past concert setlist."));
        }
    }

    /**
     * Solar API를 사용하여 특정 아티스트의 예상 셋리스트 생성 및 저장
     *
     * @param artistId 아티스트 ID
     * @return 예상 셋리스트
     */
    @GetMapping("/predict/artist/{artistId}")
    public ResponseEntity<?> generateAndSavePredictedSetlist(@PathVariable Long artistId) {
        try {
            // 1. 예상 셋리스트 생성
            String predictedSetlist = concertSetlistService.generatePredictedSetlist(artistId).block();
            if (predictedSetlist == null || predictedSetlist.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No predicted setlist available.");
            }

            // 2. JSON 형태로 변환
            List<Map<String, Object>> setlistJson = Arrays.stream(predictedSetlist.split("\n"))
                    .filter(line -> !line.trim().isEmpty() && !line.contains("예상 셋리스트:"))
                    .filter(line -> !line.toLowerCase().contains("intro"))
                    .map(line -> parseSetlistLine(line, artistId))
                    .filter(Objects::nonNull)
                    .toList();

            // 3. NewConcert 생성 및 저장
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new IllegalArgumentException("Artist with ID " + artistId + " not found."));

            NewConcert newConcert = NewConcert.builder()
                    .artistId(artist)
                    .title("Predicted Concert")
                    .date(LocalDate.now())
                    .venueName("TBD")
                    .cityName("TBD")
                    .countryName("TBD")
                    .countryCode("TBD")
                    .concertStatus(NewConcert.ConcertStatus.UPCOMING)
                    .build();

            newConcertRepository.save(newConcert);

            // 4. 예상 셋리스트 저장
            concertSetlistService.savePredictedSetlistToDatabase(newConcert, setlistJson);

            return ResponseEntity.ok(setlistJson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while generating the predicted setlist."));
        }
    }

    // JSON 변환 유틸리티
    private Map<String, Object> parseSetlistLine(String line, Long artistId) {
        try {
            // 점(". ") 기준으로 분리
            String[] parts = line.split("\\. ", 2);

            // 첫 번째 부분을 숫자로 변환 가능 여부 확인
            if (parts.length < 2 || !isNumeric(parts[0].trim())) {
                System.err.println("Invalid line format: " + line);
                return null;
            }

            // 순서와 제목 파싱
            int order = Integer.parseInt(parts[0].trim());
            String trimmedTitle = parts[1].trim();

            // 곡 정보 조회
            Optional<Song> songOpt = songRepository.findByTitleAndArtistId(trimmedTitle, artistId);

            Map<String, Object> songData = new LinkedHashMap<>();
            songData.put("title", trimmedTitle);
            songData.put("songId", songOpt.map(Song::getSongId).orElse(null));
            songData.put("order", order);
            songData.put("ytLink", songOpt.map(Song::getYtLink).orElse(null));
            return songData;

        } catch (Exception e) {
            System.err.println("Error parsing line: " + line);
            e.printStackTrace();
            return null;
        }
    }

    // 유틸리티: 숫자 여부 확인
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

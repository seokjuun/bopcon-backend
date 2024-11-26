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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving past setlists."));
        }
    }

    /**
     * 특정 PastConcert의 셋리스트 조회
     */
    @GetMapping("/past-concert/{pastConcertId}")
    public ResponseEntity<?> getSetlistByPastConcertId(@PathVariable Long pastConcertId) {
        try {
            // PastConcert 조회
            PastConcert pastConcert = concertSetlistService.findPastConcertById(pastConcertId);

            // Setlist 조회
            List<SetlistDTO> setlist = concertSetlistService.getSetlistByPastConcertId(pastConcertId);

            // 응답 데이터 구성
            Map<String, Object> response = Map.of(
                    "artistId", pastConcert.getArtistId().getArtistId(),
                    "setlist", setlist
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while retrieving the past concert setlist."));
        }
    }


    /**
     * Solar API를 사용하여 특정 아티스트의 예상 셋리스트 생성 및 저장
     */
    @GetMapping("/predict/artist/{artistId}")
    public ResponseEntity<?> generateAndSavePredictedSetlist(@PathVariable Artist artistId) {
        try {
            // 1. 예상 셋리스트 생성
            String predictedSetlist = concertSetlistService.generatePredictedSetlist(artistId.getArtistId()).block();
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

            // 3. 기존 예상 셋리스트 삭제
            List<NewConcert> existingConcerts = newConcertRepository.findByArtist_ArtistIdAndConcertStatus(
                    artistId.getArtistId(), NewConcert.ConcertStatus.UPCOMING
            );

            if (existingConcerts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No UPCOMING concerts found for artist ID: " + artistId);
            }

// 첫 번째 콘서트를 사용하거나 적합한 논리를 추가하여 선택
            NewConcert selectedConcert = existingConcerts.get(0);

// 기존 예상 셋리스트 삭제
            concertSetlistService.deleteSetlistForConcert(selectedConcert.getNewConcertId());

// 새로운 예상 셋리스트 저장
            concertSetlistService.savePredictedSetlistToDatabase(selectedConcert, setlistJson);

            return ResponseEntity.ok(setlistJson);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while generating the predicted setlist."));
        }
    }




    @GetMapping("/predict/new-concert/{newConcertId}")
    public ResponseEntity<?> generateOrFetchPredictedSetlist(@PathVariable Long newConcertId) {
        try {
            // 1. NewConcert 조회
            NewConcert newConcert = newConcertRepository.findById(newConcertId)
                    .orElseThrow(() -> new IllegalArgumentException("NewConcert with ID " + newConcertId + " not found."));

            // 2. 데이터베이스에서 예상 셋리스트 존재 여부 확인
            List<SetlistDTO> existingSetlist = concertSetlistService.getSetlistByNewConcertId(newConcertId);
            if (!existingSetlist.isEmpty()) {
                // 예상 셋리스트가 이미 존재할 경우 반환
                return ResponseEntity.ok(existingSetlist);
            }

            // 3. 아티스트 정보 조회
            Artist artist = newConcert.getArtistId();
            if (artist == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Artist not associated with NewConcert ID: " + newConcertId));
            }

            // 4. 외부 API 호출을 통해 예상 셋리스트 생성
            String predictedSetlist = concertSetlistService.generatePredictedSetlist(artist.getArtistId()).block();
            if (predictedSetlist == null || predictedSetlist.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No predicted setlist available.");
            }

            // 5. JSON 형태로 변환
            List<Map<String, Object>> setlistJson = Arrays.stream(predictedSetlist.split("\n"))
                    .filter(line -> !line.trim().isEmpty() && !line.contains("예상 셋리스트:"))
                    .filter(line -> !line.toLowerCase().contains("intro"))
                    .map(line -> parseSetlistLine(line, artist))
                    .filter(Objects::nonNull)
                    .toList();

            // 6. 새로운 예상 셋리스트 저장
            concertSetlistService.savePredictedSetlistToDatabase(newConcert, setlistJson);

            return ResponseEntity.ok(setlistJson);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while generating or fetching the predicted setlist."));
        }
    }

    @PatchMapping("/new-concert/{newConcertId}")
    public ResponseEntity<?> updateConcertStatus(
            @PathVariable Long newConcertId,
            @RequestParam NewConcert.ConcertStatus status) {
        try {
            concertSetlistService.handleConcertSetlistStatus(newConcertId, status);
            return ResponseEntity.ok(Map.of("message", "Concert status updated successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while updating concert status."));
        }
    }

    // JSON 변환 유틸리티
    private Map<String, Object> parseSetlistLine(String line, Artist artistId) {
        try {
            String[] parts = line.split("\\. ", 2);

            if (parts.length < 2 || !isNumeric(parts[0].trim())) {
                System.err.println("Invalid line format: " + line);
                return null;
            }

            int order = Integer.parseInt(parts[0].trim());
            String trimmedTitle = parts[1].trim();

            // 변경된 메서드 사용
            Optional<Song> songOpt = songRepository.findFirstByTitleAndArtistId(trimmedTitle, artistId);

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

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

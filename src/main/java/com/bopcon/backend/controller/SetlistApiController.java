package com.bopcon.backend.controller;

import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.SetlistDTO;
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

    @Autowired
    public SetlistApiController(ConcertSetlistService concertSetlistService, SongRepository songRepository) {
        this.concertSetlistService = concertSetlistService;
        this.songRepository = songRepository;
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
            // 에러 메시지를 자세히 출력
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }


    /**
     * 특정 PastConcert의 셋리스트 조회
     *
     * @param pastConcertId 과거 콘서트 ID
     * @return PastConcert에 포함된 셋리스트
     */
    @GetMapping("/past-concert/{pastConcertId}")
    public ResponseEntity<?> getSetlistByPastConcertId(@PathVariable PastConcert pastConcertId) {
        try {
            List<SetlistDTO> setlist = concertSetlistService.getSetlistByPastConcertId(pastConcertId);
            return ResponseEntity.ok(setlist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while retrieving the setlist.");
        }
    }

    /**
     * Solar API를 사용하여 특정 아티스트의 예상 셋리스트 생성
     *
     * @param artistId 아티스트 ID
     * @return 예상 셋리스트
     */
    @GetMapping("/predict/artist/{artistId}")
    public ResponseEntity<?> generatePredictedSetlist(@PathVariable Long artistId) {
        try {
            // 예상 셋리스트 생성
            String predictedSetlist = concertSetlistService.generatePredictedSetlist(artistId).block(); // 비동기 처리

            // 예상 셋리스트를 JSON 형태로 반환
            List<Map<String, Object>> setlistJson = Arrays.stream(predictedSetlist.split("\n"))
                    .filter(line -> !line.trim().isEmpty() && !line.contains("예상 셋리스트:")) // "예상 셋리스트:" 제거
                    .filter(line -> !line.toLowerCase().contains("intro")) // "Intro"를 필터링
                    .map(line -> {
                        try {
                            // 곡 제목에서 순서 번호와 제목 분리
                            String[] parts = line.split("\\. ", 2);
                            int order = Integer.parseInt(parts[0].trim());
                            String trimmedTitle = parts[1].trim();

                            // 곡 정보를 조회
                            Optional<Song> songOpt = songRepository.findByTitleAndArtistId(trimmedTitle, artistId);

                            // 곡 데이터 생성
                            Map<String, Object> songData = new LinkedHashMap<>();
                            songData.put("title", trimmedTitle); // title 먼저 추가
                            songData.put("songId", songOpt.map(Song::getSongId).orElse(null)); // songId 추가
                            songData.put("order", order); // order 추가
                            songData.put("ytLink", songOpt.map(Song::getYtLink).orElse(null)); // ytLink 마지막에 추가
                            return songData;
                        } catch (Exception e) {
                            // 파싱 실패 시 로그 출력
                            System.err.println("Error parsing line: " + line);
                            e.printStackTrace();
                            return null; // null을 반환
                        }
                    })
                    .filter(Objects::nonNull) // null 값 필터링
                    .toList();

            return ResponseEntity.ok(setlistJson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while generating the predicted setlist."));
        }
    }






    // 숫자인지 확인하는 유틸리티 메서드
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }








}

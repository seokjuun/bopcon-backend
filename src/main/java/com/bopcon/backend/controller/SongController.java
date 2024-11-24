package com.bopcon.backend.controller;

import com.bopcon.backend.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/song-ranking")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    /**
     * 특정 아티스트의 곡 랭킹을 가져오는 API
     *
     * @param mbid 아티스트의 MusicBrainz ID
     * @return 곡 제목과 카운트로 이루어진 Map
     */
    @GetMapping("/artist/{mbid}")
    public ResponseEntity<?> getSongRanking(@PathVariable String mbid) {
        try {
            // SongService에서 랭킹 계산
            Map<String, Integer> rankedSongs = songService.fetchAndRankSongs(mbid);

            // 성공적으로 데이터 반환
            return ResponseEntity.ok(rankedSongs);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 MBID로 인한 예외 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid MBID provided: " + e.getMessage()));
        } catch (Exception e) {
            // 서버 내부 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch and rank songs: " + e.getMessage()));
        }
    }
}

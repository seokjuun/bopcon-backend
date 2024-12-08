package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.dto.*;
import com.bopcon.backend.service.ArtistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArtistApiController {
    private final ArtistService artistService;
    private final ObjectMapper objectMapper;

    // 등록
    @PostMapping("/api/admin/artist")
    public ResponseEntity<Artist> addArtist(@RequestBody AddArtistRequest request) {
        Artist savedArtist = artistService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArtist);
    }
    // 아티스트 전체 조회
    @GetMapping("/api/artists")
    public ResponseEntity<List<ArtistResponse>> findAllArtists() {
        List<ArtistResponse> artists = artistService.findAllArtists()
                .stream()
                .map(ArtistResponse::new)
                .toList();
        return ResponseEntity.ok().body(artists);
    }

    // 아티스트 조회
    @GetMapping("/api/artists/{artistId}")
    public ResponseEntity<ArtistResponse> findArtist(@PathVariable long artistId) {
        Artist artist = artistService.findByArtistId(artistId);
        return ResponseEntity.ok().body(new ArtistResponse(artist));
    }

    // 아티스트 수정
    @PutMapping("/api/admin/artists/{artistId}")
    public ResponseEntity<Artist> updateArtist(@PathVariable long artistId, @RequestBody UpdateArtistRequest request) {
        Artist updateArtist = artistService.update(artistId, request);
        return ResponseEntity.ok().body(updateArtist);
    }

    // 아티스트 삭제
    @DeleteMapping("/api/admin/artists/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable long artistId) {
        artistService.delete(artistId);

        return ResponseEntity.ok().build(); // build() 본문이 없는 응답 생성
    }

    // 1. 특정 아티스트의 과거 공연 데이터 반환
    @GetMapping("/api/artists/{artistId}/past-concerts")
    public ResponseEntity<List<PastConcertDTO>> getPastConcerts(@PathVariable Long artistId) {
        List<PastConcertDTO> pastConcerts = artistService.getArtistPastConcerts(artistId);
        return ResponseEntity.ok(pastConcerts);
    }

    // 2. 외부 API를 통해 특정 아티스트 데이터 동기화
    @PostMapping("/api/admin/artists/{mbid}/sync")
    public ResponseEntity<String> syncArtistData(@PathVariable String mbid) {
        artistService.syncArtistData(mbid);
        return ResponseEntity.ok("Artist data synced successfully");
    }

    // 특정 아티스트의 곡 랭킹 반환
    @GetMapping("/api/artists/{artistId}/song-ranking")
    public ResponseEntity<List<SongRankingDTO>> getSongRanking(@PathVariable Long artistId) {
        List<SongRankingDTO> songRanking = artistService.getSongRankingByArtist(artistId);
        return ResponseEntity.ok(songRanking);
    }

    // 특정 아티스트의 과거 콘서트 셋리스트 반환
    @GetMapping("/api/artists/{artistId}/past-setlists")
    public ResponseEntity<List<PastConcertSetlistDTO>> getPastConcertSetlists(@PathVariable Long artistId) {
        List<PastConcertSetlistDTO> setlists = artistService.getPastConcertSetlistsByArtist(artistId);
        return ResponseEntity.ok(setlists);
    }
    // 특정 아티스트의 내한 콘서트 예상 셋리스트 생성
    @PostMapping("/api/admin/artists/{artistId}/predict-setlist")
    public ResponseEntity<String> predictSetlist(@PathVariable Long artistId,
                                                 @RequestParam Long newConcertId) {
        // 1. 과거 셋리스트 데이터를 내부적으로 호출
        List<PastConcertSetlistDTO> pastSetlists = artistService.getPastConcertSetlistsByArtist(artistId);

        // 2. 과거 셋리스트 데이터를 JSON으로 변환
        String pastSetlistJson;
        try {
            pastSetlistJson = objectMapper.writeValueAsString(pastSetlists);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert past setlists to JSON", e);
        }

        // 3. 예상 셋리스트 생성 및 저장
        artistService.processPredictedSetlist(artistId, newConcertId, pastSetlistJson);

        return ResponseEntity.ok("Predicted setlist saved successfully");
    }

    // 특정 콘서트 예상 셋리스트 조회
    @GetMapping("/api/new-concerts/{newConcertId}/predicted-setlist")
    public ResponseEntity<List<PredictSetlistDTO>> getPredictedSetlist(@PathVariable Long newConcertId) {
        List<PredictSetlistDTO> predictedSetlist = artistService.getPredictedSetlist(newConcertId);
        return ResponseEntity.ok(predictedSetlist);
    }
}

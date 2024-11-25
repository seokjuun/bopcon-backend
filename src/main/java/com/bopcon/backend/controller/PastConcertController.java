package com.bopcon.backend.controller;


import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.dto.ArtistResponse;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.PastConcertResponse;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.service.PastConcertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/past-concerts")
public class PastConcertController {

    private final PastConcertService pastConcertService;
    private final ArtistRepository artistRepository;


    @Autowired
    public PastConcertController(PastConcertService pastConcertService, ArtistRepository artistRepository) {
        this.pastConcertService = pastConcertService;
        this.artistRepository = artistRepository;
    }

    // 과거 콘서트 전체 조회
    @GetMapping
    public ResponseEntity<List<PastConcertResponse>> getAllPastConcerts() {
        List<PastConcertResponse> concerts = pastConcertService.getAllPastConcerts();
        return ResponseEntity.ok(concerts);
    }

//    // 특정 아티스트의 과거 콘서트 조회
//    @GetMapping("/artist/{mbid}")
//    public List<PastConcert> getPastConcertsByArtist(@PathVariable String mbid) {
//        return pastConcertService.getPastConcertsByArtist(mbid);
//    }

    // 특정 콘서트 상세 조회
    @GetMapping("/{pastConcertId}")
    public PastConcert getPastConcertById(@PathVariable Long pastConcertId) {
        return pastConcertService.getPastConcertById(pastConcertId);
    }

//    @GetMapping("/artist/id/{id}")
//    public ResponseEntity<?> getArtistById(@PathVariable("id") Long id) {
//        try {
//            log.info("Fetching artist with ID: {}", id);
//            return artistRepository.findById(id)
//                    .map(artist -> {
//                        log.info("Artist found: {}", artist.getName());
//                        return ResponseEntity.ok(new ArtistResponse(artist));
//                    })
//                    .orElseGet(() -> {
//                        log.warn("Artist not found with ID: {}", id);
//                        return ResponseEntity.status(404).body((ArtistResponse) Map.of("error", "Artist not found with ID: " + id));
//                    });
//        } catch (Exception e) {
//            log.error("Error fetching artist with ID: {}", id, e);
//            return ResponseEntity.status(500).body(Map.of("error", "Internal server error occurred"));
//        }
//    }





    @GetMapping("/artist/{artistName}")
    public ResponseEntity<?> getPastConcertsByArtistName(@PathVariable String artistName) {
        try {
            List<PastConcertDTO> pastConcerts = pastConcertService.getPastConcertsByArtistName(artistName);
            return ResponseEntity.ok(pastConcerts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }



//    @GetMapping("/concerts")
//    public ResponseEntity<List<PastConcert>> getConcerts(@RequestParam String name) {
//        List<PastConcert> concerts = pastConcertService.getPastConcertsByArtistName(name);
//        return ResponseEntity.ok(concerts);
//    }



    @PostMapping("/fetch")
    public ResponseEntity<?> fetchAndSaveConcerts(@RequestParam String mbid) {
        try {
            // 로그로 MBID 확인
            log.info("Fetching past concerts for MBID: {}", mbid);

            // 과거 콘서트 정보 저장 로직 실행
            pastConcertService.fetchAndSavePastConcerts(mbid);

            // 성공 응답 반환
            return ResponseEntity.ok(Map.of("message", "Past concerts fetched and saved successfully."));
        } catch (Exception e) {
            // 에러 발생 시 로그 기록
            log.error("Error while saving past concerts for MBID: {}", mbid, e);

            // 실패 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save past concerts: " + e.getMessage()));
        }
    }

}







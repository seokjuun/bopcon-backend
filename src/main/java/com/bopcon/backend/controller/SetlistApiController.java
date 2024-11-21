package com.bopcon.backend.controller;

import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.SetlistDTO;
import com.bopcon.backend.service.ConcertSetlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setlists")
public class SetlistApiController {

    private final ConcertSetlistService concertSetlistService;

    @Autowired
    public SetlistApiController(ConcertSetlistService concertSetlistService) {
        this.concertSetlistService = concertSetlistService;
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
}

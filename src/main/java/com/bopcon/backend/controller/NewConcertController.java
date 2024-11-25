package com.bopcon.backend.controller;

import com.bopcon.backend.dto.NewConcertResponse;
import com.bopcon.backend.service.NewConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/new-concerts")
public class NewConcertController {

    private final NewConcertService newConcertService;

    // 🔥 타이틀 검색 엔드포인트
    @GetMapping("/search/title")
    public ResponseEntity<List<NewConcertResponse>> searchConcertsByTitle(@RequestParam String title) {
        List<NewConcertResponse> responses = newConcertService.findNewConcertsByTitle(title).stream()
                .map(NewConcertResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // 🔥 장르 검색 엔드포인트
    @GetMapping("/search/genre")
    public ResponseEntity<List<NewConcertResponse>> searchConcertsByGenre(@RequestParam String genre) {
        List<NewConcertResponse> responses = newConcertService.findNewConcertsByGenre(genre).stream()
                .map(NewConcertResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // 🔥 도시 이름으로 검색 엔드포인트
    @GetMapping("/search/city")
    public ResponseEntity<List<NewConcertResponse>> searchConcertsByCity(@RequestParam String cityName) {
        List<NewConcertResponse> responses = newConcertService.findNewConcertsByCityName(cityName).stream()
                .map(NewConcertResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}

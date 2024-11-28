package com.bopcon.backend.controller;

import com.bopcon.backend.dto.SearchResponse;
import com.bopcon.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/api/search")
    public ResponseEntity<List<SearchResponse.ConcertResponse>> searchByKeyword(@RequestParam String keyword) {
        List<SearchResponse.ConcertResponse> results = searchService.searchByKeyword(keyword);
        return ResponseEntity.ok(results);
    }
}

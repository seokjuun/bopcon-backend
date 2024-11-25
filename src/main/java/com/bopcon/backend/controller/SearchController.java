package com.bopcon.backend.controller;

import com.bopcon.backend.dto.SearchResponse;
import com.bopcon.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    // 🔥 키워드 검색 API
    @GetMapping
    public ResponseEntity<SearchResponse> search(@RequestParam String keyword) {
        SearchResponse response = searchService.searchByKeyword(keyword);
        return ResponseEntity.ok().body(response);
    }
}

package com.bopcon.backend.service;

import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.SearchResponse;
import com.bopcon.backend.repository.NewConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final NewConcertRepository newConcertRepository;

    public List<SearchResponse.ConcertResponse> searchByKeyword(String keyword) {
        // NewConcertRepository에서 키워드로 검색
        List<NewConcert> concerts = newConcertRepository.findByTitleContainingIgnoreCaseOrVenueNameContainingIgnoreCaseOrGenreContainingIgnoreCase(keyword, keyword, keyword);

        // ConcertResponse로 변환하여 반환
        return concerts.stream()
                .map(SearchResponse.ConcertResponse::new)
                .collect(Collectors.toList());
    }
}

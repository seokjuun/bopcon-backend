package com.bopcon.backend.service;

import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.SearchResponse;
import com.bopcon.backend.repository.NewConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final NewConcertRepository newConcertRepository;

    public List<SearchResponse.ConcertResponse> searchByKeyword(String keyword) {
        List<NewConcert> concerts = newConcertRepository.findAll(); // 전체 콘서트 조회

        return concerts.stream()
                .filter(concert -> matchesKeyword(concert, keyword)) // 키워드로 필터링
                .map(SearchResponse.ConcertResponse::new) // DTO 변환
                .collect(Collectors.toList());
    }

    private boolean matchesKeyword(NewConcert concert, String keyword) {
        return containsIgnoreCase(concert.getTitle(), keyword) || // 콘서트 제목
                containsIgnoreCase(concert.getCityName(), keyword) || // 도시
                containsIgnoreCase(concert.getGenre(), keyword) || // 장르
                containsIgnoreCase(concert.getVenueName(), keyword) || // 공연장
                containsIgnoreCase(concert.getArtist().getKrName(), keyword) || // 아티스트 한글 이름
                containsIgnoreCase(concert.getArtist().getName(), keyword); // 아티스트 영어 이름
    }

    private boolean containsIgnoreCase(String field, String keyword) {
        return field != null && field.toLowerCase().contains(keyword.toLowerCase());
    }
}

package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.SearchResponse;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.NewConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final ArtistRepository artistRepository;
    private final NewConcertRepository newConcertRepository;

    // 🔥 키워드로 검색
    public SearchResponse searchByKeyword(String keyword) {
        // 1️⃣ 키워드가 아티스트 이름에 해당되는지 확인
        Artist artist = artistRepository.findByNameContainingIgnoreCaseOrKrNameContainingIgnoreCase(keyword, keyword)
                .orElse(null);

        if (artist != null) {
            // 해당 아티스트의 콘서트 정보 반환
            List<NewConcert> concerts = newConcertRepository.findByArtist(artist);
            return new SearchResponse(artist, concerts);
        }

        // 2️⃣ 아티스트가 아니면 기존 콘서트 검색 동작 수행
        List<NewConcert> concerts = newConcertRepository.findByTitleContainingIgnoreCaseOrVenueNameContainingIgnoreCaseOrGenreContainingIgnoreCase(
                keyword, keyword, keyword);
        return new SearchResponse(concerts);
    }
}

package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.dto.UpdateNewConcertRequest;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.NewConcertRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NewConcertService {

    private final NewConcertRepository newConcertRepository;
    private final ArtistRepository artistRepository;

    // 🔥 뉴 콘서트 추가 메서드
    public NewConcert save(AddNewConcertRequest request) {
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with ID: " + request.getArtistId()));
        return newConcertRepository.save(request.toNewConcert(artist));
    }

    // 🔥 뉴 콘서트 수정 메서드
    @Transactional
    public NewConcert update(long newConcertId, UpdateNewConcertRequest request) {
        NewConcert newConcert = newConcertRepository.findById(newConcertId)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found with ID: " + newConcertId));

        newConcert.updateNewConcert(request);
        return newConcert;
    }

    // 🔥 모든 콘서트 목록 가져오기
    public List<NewConcert> findAllNewConcerts() {
        return newConcertRepository.findAll();
    }

    // 🔥 장르별로 콘서트 검색
    public List<NewConcert> findNewConcertsByGenre(String genre) {
        if (genre == null || genre.isEmpty()) {
            throw new IllegalArgumentException("Genre must not be null or empty");
        }
        return newConcertRepository.findByGenreContainingIgnoreCase(genre);
    }

    // 🔥 콘서트 ID로 조회
    public NewConcert findByConcertId(long concertId) {
        return newConcertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("Concert not found with ID: " + concertId));
    }

    // 🔥 콘서트 삭제
    public void delete(long concertId) {
        if (!newConcertRepository.existsById(concertId)) {
            throw new IllegalArgumentException("Concert not found with ID: " + concertId);
        }
        newConcertRepository.deleteById(concertId);
    }
}

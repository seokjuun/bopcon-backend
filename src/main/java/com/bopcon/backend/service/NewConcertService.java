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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class NewConcertService {
    private final NewConcertRepository newConcertRepository;
    private final ArtistRepository artistRepository;

    // 뉴 콘서트 추가 메서드
    @CacheEvict(value = {"allNewConcerts", "newConcertsByGenre"}, allEntries = true) // 관련 캐시 무효화
    public NewConcert save(AddNewConcertRequest request) {
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(()-> new EntityNotFoundException("Artist not found"));
        return newConcertRepository.save(request.toNewConcert(artist));
    }

    // 뉴 콘서트 수정
    @Transactional
    @CacheEvict(value = {"allNewConcerts", "newConcertsByGenre"}, allEntries = true) // 관련 캐시 무효화
    public NewConcert update(long newConcertId, UpdateNewConcertRequest request){
        NewConcert newConcert = newConcertRepository.findById(newConcertId)
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ newConcertId));
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));

        newConcert.updateNewConcert(request, artist);
        return newConcert;
    }

    // 콘서트 목록 가져오기
    @Cacheable(value = "allNewConcerts", key = "'allConcerts'")
    public List<NewConcert> findAllNewConcerts(){ return newConcertRepository.findAll(); }

    // 콘서트 (장르 필터) 목록 가져오기
    @Cacheable(value = "newConcertsByGenre", key = "#genre")
    public List<NewConcert> findNewConcertsByGenre(String genre){
        return newConcertRepository.findByGenre(genre);
    }

    // 콘서트 조회
    @Cacheable(value = "singleConcert", key = "#concertId")
    public NewConcert findByConcertId(long concertId){
        return newConcertRepository.findById(concertId)
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ concertId));
    }

    // 콘서트 삭제
    @CacheEvict(value = {"allNewConcerts", "newConcertsByGenre", "singleConcert"}, allEntries = true)
    public void delete(long concertId){
        newConcertRepository.deleteById(concertId);
    }

    // 아티스트 콘서트 가져오기
    @Cacheable(value = "newConcertsByArtist", key = "#artistId")
    public List<NewConcert> findNewConcertsByArtistId(Long artistId){
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()->new IllegalArgumentException("Invalid artistId: "+ artistId));

        return newConcertRepository.findByArtist(artist);
    }
}

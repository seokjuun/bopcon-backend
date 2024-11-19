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

    // 뉴 콘서트 추가 메서드
    public NewConcert save(AddNewConcertRequest request) {
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(()-> new EntityNotFoundException("Artist not found"));
        return newConcertRepository.save(request.toNewConcert(artist));
    }

    // 뉴 콘서트 수정
    @Transactional
    public NewConcert update(long newConcertId, UpdateNewConcertRequest request){
        NewConcert newConcert = newConcertRepository.findById(newConcertId)
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ newConcertId));

        newConcert.updateNewConcert(request);
        return newConcert;
    }

    // 콘서트 목록 가져오기
    public List<NewConcert> findAllNewConcerts(){ return newConcertRepository.findAll(); }

    // 콘서트 (장르 필터) 목록 가져오기
    public List<NewConcert> findNewConcertsByGenre(String genre){
        return newConcertRepository.findByGenre(genre);
    }
    // 콘서트 조회
    public NewConcert findByConcertId(long concertId){
        return newConcertRepository.findById(concertId)
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ concertId));
    }

    // 콘서트 삭제
    public void delete(long concertId){
        newConcertRepository.deleteById(concertId);
    }

    // 아티스트 콘서트 가져오기
    public List<NewConcert> findNewConcertsByArtistId(Long artistId){
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()->new IllegalArgumentException("Invalid artistId: "+ artistId));

        return newConcertRepository.findByArtist(artist);
    }
}
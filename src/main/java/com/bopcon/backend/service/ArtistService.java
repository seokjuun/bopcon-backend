package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.dto.AddArtistRequest;
import com.bopcon.backend.dto.UpdateArtistRequest;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.NewConcertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArtistService {
    private final ArtistRepository artistRepository;



    // 아티스트 추가 메서드
    public Artist save(AddArtistRequest request) {  return artistRepository.save(request.toArtist());}

    // 아티스트 목록 가져오기
    public List<Artist> findAllArtists(){ return artistRepository.findAll(); }

    // 아티스트 조회
    public Artist findByArtistId(long artistId){
        return artistRepository.findById(artistId)
                .orElseThrow(()-> new IllegalArgumentException("not found" + artistId));
    }

    // 아시트스 수정
    @Transactional
    public Artist update(long artistId, UpdateArtistRequest request) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()-> new IllegalArgumentException("not found" + artistId));

        artist.updateArtist(request);
        return artist;
    }

    // 아티스트 삭제
    public void delete(long artistId){
        artistRepository.deleteById(artistId);
    }
}

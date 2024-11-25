package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.dto.AddArtistRequest;
import com.bopcon.backend.dto.UpdateArtistRequest;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.NewConcertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArtistService {
    private final ArtistRepository artistRepository;

    // 아티스트 추가 메서드
    @CacheEvict(value = {"allArtists", "singleArtist"}, allEntries = true)
    public Artist save(AddArtistRequest request) {  return artistRepository.save(request.toArtist());}

    // 아티스트 목록 가져오기
    @Cacheable(value = "allArtists", key = "'allArtists'")
    public List<Artist> findAllArtists(){ return artistRepository.findAll(); }

    // 아티스트 조회
    @Cacheable(value = "singleArtist", key = "#artistId")
    public Artist findByArtistId(long artistId){
        return artistRepository.findById(artistId)
                .orElseThrow(()-> new IllegalArgumentException("not found" + artistId));
    }

    // 아시트스 수정
    @Transactional
    @CacheEvict(value = {"allArtists", "singleArtist"}, allEntries = true)
    public Artist update(long artistId, UpdateArtistRequest request) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()-> new IllegalArgumentException("not found" + artistId));

        artist.updateArtist(request);
        return artist;
    }

    // 아티스트 삭제
    @CacheEvict(value = {"allArtists", "singleArtist"}, allEntries = true)
    public void delete(long artistId){
        artistRepository.deleteById(artistId);
    }
}

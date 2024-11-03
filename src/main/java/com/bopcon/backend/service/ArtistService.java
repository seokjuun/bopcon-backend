package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.dto.AddArtistRequest;
import com.bopcon.backend.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArtistService {
    private final ArtistRepository artistRepository;

    // 아티스트 추가 메서드
    public Artist save(AddArtistRequest request) {  return artistRepository.save(request.toArtist());}
}

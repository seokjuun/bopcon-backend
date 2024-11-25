package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddArtistRequest;
import com.bopcon.backend.dto.ArtistResponse;
import com.bopcon.backend.dto.ArtistWithConcertsResponse;
import com.bopcon.backend.dto.UpdateArtistRequest;
import com.bopcon.backend.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArtistApiController {
    private final ArtistService artistService;

    @GetMapping("/api/artists/search")
    public ResponseEntity<ArtistWithConcertsResponse> searchArtistByName(@RequestParam String name) {
        Artist artist = artistService.findArtistWithConcerts(name);
        return ResponseEntity.ok(new ArtistWithConcertsResponse(artist));
    }


    // 등록
    @PostMapping("/api/admin/artist")
    public ResponseEntity<Artist> addArtist(@RequestBody AddArtistRequest request) {
        Artist  savedArtist = artistService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArtist);
    }
    // 아티스트 전체 조회
    @GetMapping("/api/artists")
    public ResponseEntity<List<ArtistResponse>> findAllArtists() {
        List<ArtistResponse> artists = artistService.findAllArtists()
                .stream()
                .map(ArtistResponse::new)
                .toList();
        return ResponseEntity.ok().body(artists);
    }

    // 아티스트 조회
    @GetMapping("/api/artists/{artistId}")
    public ResponseEntity<ArtistResponse> findArtist(@PathVariable long artistId) {
        Artist artist = artistService.findByArtistId(artistId);
        return ResponseEntity.ok().body(new ArtistResponse(artist));
    }

    // 아티스트 수정
    @PutMapping("/api/admin/artists/{artistId}")
    public ResponseEntity<Artist> updateArtist(@PathVariable long artistId, @RequestBody UpdateArtistRequest request) {
        Artist updateArtist = artistService.update(artistId, request);
        return ResponseEntity.ok().body(updateArtist);
    }

    // 아티스트 삭제
    @DeleteMapping("/api/artists/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable long artistId) {
        artistService.delete(artistId);

        return ResponseEntity.ok().build(); // build() 본문이 없는 응답 생성
    }
}

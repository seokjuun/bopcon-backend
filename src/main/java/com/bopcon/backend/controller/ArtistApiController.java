package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.dto.AddArtistRequest;
import com.bopcon.backend.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArtistApiController {
    private final ArtistService artistService;

    @PostMapping("/api/admin/artist")
    public ResponseEntity<Artist> addArtist(@RequestBody AddArtistRequest request) {
        Artist  savedArtist = artistService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArtist);
    }


}

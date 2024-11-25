package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ArtistWithConcertsResponse {
    private final Long artistId;
    private final String name;
    private final String krName;
    private final String imgUrl;
    private final String snsUrl;
    private final List<NewConcertResponse> concerts;

    public ArtistWithConcertsResponse(Artist artist) {
        this.artistId = artist.getArtistId();
        this.name = artist.getName();
        this.krName = artist.getKrName() != null ? artist.getKrName() : "Unknown"; // Null 처리
        this.imgUrl = artist.getImgUrl();
        this.snsUrl = artist.getSnsUrl();
        this.concerts = artist.getConcerts().stream()
                .map(NewConcertResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class ArtistResponse {

    private final String mbid;
    private final String name;
    private final String imgUrl;
    private final String snsUrl;
    private final String mediaUrl;

    public ArtistResponse(Artist artist){
        this.mbid = artist.getMbid();
        this.name = artist.getName();
        this.imgUrl = artist.getImgUrl();
        this.snsUrl = artist.getSnsUrl();
        this.mediaUrl = artist.getMediaUrl();
    }
}

package com.bopcon.backend.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateArtistRequest {
    private String mbid;
    private String name;
    private String krName;
    private String imgUrl;
    private String snsUrl;
    private String mediaUrl;
}

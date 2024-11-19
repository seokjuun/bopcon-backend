package com.bopcon.backend.dto;

import com.bopcon.backend.domain.NewConcert;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NewConcertSimpleResponse {
    private final Long newConcertId;
    private final Long artistId;
    private final String artistName;
    private final String title;
    private final LocalDate date;


    public NewConcertSimpleResponse(NewConcert newConcert){
        this.newConcertId = newConcert.getNewConcertId();
        this.artistId = newConcert.getArtist().getArtistId();
        this.artistName = newConcert.getArtist().getName();
        this.title = newConcert.getTitle();
        this.date = newConcert.getDate();
    }
}

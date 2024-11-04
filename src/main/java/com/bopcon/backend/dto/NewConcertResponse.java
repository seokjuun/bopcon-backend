package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.service.NewConcertService;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NewConcertResponse {
    private final Artist artistId;
    private final String title;
    private final String subTitle;
    private final LocalDate date;
    private final String venueName; // 공연장
    private final String cityName;
    private final String countryName; // ex) Republic of Korea
    private final String countryCode; // ex) Kr
    private final String ticketPlatforms;
    private final String ticketUrl;
    private final String posterUrl; // 포스터
    private final String genre;
    private final NewConcert.ConcertStatus concertStatus;

    public NewConcertResponse(NewConcert newConcert){
        this.artistId = newConcert.getArtistId();
        this.title = newConcert.getTitle();
        this.subTitle = newConcert.getSubTitle();
        this.date = newConcert.getDate();
        this.venueName = newConcert.getVenueName();
        this.cityName = newConcert.getCityName();
        this.countryName = newConcert.getCountryName();
        this.countryCode = newConcert.getCountryCode();
        this.ticketPlatforms = newConcert.getTicketPlatforms();
        this.ticketUrl = newConcert.getTicketUrl();
        this.posterUrl = newConcert.getPosterUrl();
        this.genre = newConcert.getGenre();
        this.concertStatus = newConcert.getConcertStatus();
    }
}

package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchResponse {
    private final Long artistId; // 아티스트 ID
    private final String name; // 아티스트 이름
    private final String krName; // 아티스트 한글 이름
    private final String imgUrl; // 아티스트 이미지 URL
    private final String snsUrl; // 아티스트 SNS URL
    private final List<ConcertResponse> concerts; // 콘서트 정보 리스트

    public SearchResponse(Artist artist, List<NewConcert> concerts) {
        this.artistId = artist.getArtistId();
        this.name = artist.getName();
        this.krName = artist.getKrName();
        this.imgUrl = artist.getImgUrl();
        this.snsUrl = artist.getSnsUrl();
        this.concerts = concerts.stream()
                .map(ConcertResponse::new)
                .collect(Collectors.toList());
    }

    public SearchResponse(List<NewConcert> concerts) {
        this.artistId = null;
        this.name = null;
        this.krName = null;
        this.imgUrl = null;
        this.snsUrl = null;
        this.concerts = concerts.stream()
                .map(ConcertResponse::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class ConcertResponse {
        private final Long newConcertId;
        private final String title;
        private final String subTitle;
        private final String date;
        private final String venueName;
        private final String cityName;
        private final String countryName;
        private final String countryCode;
        private final String ticketPlatforms;
        private final String ticketUrl;
        private final String posterUrl;
        private final String genre;
        private final String concertStatus;

        public ConcertResponse(NewConcert concert) {
            this.newConcertId = concert.getNewConcertId();
            this.title = concert.getTitle();
            this.subTitle = concert.getSubTitle();
            this.date = concert.getDate().toString();
            this.venueName = concert.getVenueName();
            this.cityName = concert.getCityName();
            this.countryName = concert.getCountryName();
            this.countryCode = concert.getCountryCode();
            this.ticketPlatforms = concert.getTicketPlatforms();
            this.ticketUrl = concert.getTicketUrl();
            this.posterUrl = concert.getPosterUrl();
            this.genre = concert.getGenre();
            this.concertStatus = concert.getConcertStatus().name();
        }
    }
}

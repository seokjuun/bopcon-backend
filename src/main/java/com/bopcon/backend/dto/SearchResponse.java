package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchResponse {


    @Getter
    public static class ConcertResponse {
        private final Long newConcertId;
        private final String title;
        private final String subTitle;
        private final String startDate;
        private final String endDate;
        private final String venueName;
        private final String cityName;
        private final String countryName;
        private final String countryCode;
        private final String ticketPlatforms;
        private final String ticketUrl;
        private final String posterUrl;
        private final String genre;
        private final String concertStatus;
        private final Long artistId;
        private final String artistName;
        private final String artistkrName;
        private final String imgUrl;
        private final String snsUrl;

        public ConcertResponse(NewConcert concert) {
            this.newConcertId = concert.getNewConcertId();
            this.title = concert.getTitle();
            this.subTitle = concert.getSubTitle();
            this.startDate = concert.getStartDate().toString();
            this.endDate = concert.getEndDate().toString();
            this.venueName = concert.getVenueName();
            this.cityName = concert.getCityName();
            this.countryName = concert.getCountryName();
            this.countryCode = concert.getCountryCode();
            this.ticketPlatforms = concert.getTicketPlatforms();
            this.ticketUrl = concert.getTicketUrl();
            this.posterUrl = concert.getPosterUrl();
            this.genre = concert.getGenre();
            this.concertStatus = concert.getConcertStatus().name();
            this.artistId = concert.getArtistId().getArtistId();
            this.artistName = concert.getArtistId().getName();
            this.artistkrName = concert.getArtistId().getKrName();
            this.imgUrl = concert.getArtistId().getImgUrl();
            this.snsUrl = concert.getArtistId().getSnsUrl();
        }
    }
}

package com.bopcon.backend.dto;

import com.bopcon.backend.domain.NewConcert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor // Jackson 직렬화를 위한 기본 생성자
public class NewConcertResponse {
    private Long newConcertId;
    private Long artistId;
    private String title;
    private String subTitle;
    private LocalDate date;
    private String venueName; // 공연장
    private String cityName;
    private String countryName; // ex) Republic of Korea
    private String countryCode; // ex) Kr
    private String ticketPlatforms;
    private String ticketUrl;
    private String posterUrl; // 포스터
    private String genre;
    private NewConcert.ConcertStatus concertStatus;

    // 정적 팩토리 메서드: 엔티티 -> DTO 변환
    public static NewConcertResponse fromEntity(NewConcert newConcert) {
        return new NewConcertResponse(
                newConcert.getNewConcertId(),
                newConcert.getArtistId() != null ? newConcert.getArtistId().getArtistId() : null, // Null 처리
                newConcert.getTitle(),
                newConcert.getSubTitle(),
                newConcert.getDate(),
                newConcert.getVenueName(),
                newConcert.getCityName(),
                newConcert.getCountryName(),
                newConcert.getCountryCode(),
                newConcert.getTicketPlatforms(),
                newConcert.getTicketUrl(),
                newConcert.getPosterUrl(),
                newConcert.getGenre(),
                newConcert.getConcertStatus()
        );
    }

    // 기존 엔티티 생성자를 유지하는 경우
    public NewConcertResponse(NewConcert newConcert) {
        this.newConcertId = newConcert.getNewConcertId();
        this.artistId = newConcert.getArtistId() != null ? newConcert.getArtistId().getArtistId() : null; // Null 처리
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

package com.bopcon.backend.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@EntityListeners(AuditingEntityListener.class) // 엔티티의 생성 및 수정 시간을 자동으로 감시하고 기록
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewConcert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "newconcert_id", updatable = false, nullable = false)
    private Long newConcertId;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artistId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "sub_title", length = 100)
    private String subTitle;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "venue_name", nullable = false, length = 100)
    private String venueName; // 공연장

    @Column(name = "city_name", nullable = false, length = 50)
    private String cityName;

    @Column(name = "country_name", nullable = false, length = 50)
    private String countryName; // ex) Republic of Korea

    @Column(name = "country_code", nullable = false, length = 5)
    private String countryCode; // ex) Kr

    @Column(name = "ticket_platforms", length = 255)
    private String ticketPlatforms;

    @Column(name = "ticket_url", length = 255)
    private String ticketUrl;
    @Column(name = "image_url", length = 255)
    private String imageUrl; // 포스터
    @Column(name = "genre", length = 50)
    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "concert_status", nullable = false)
    private NewConcert.ConcertStatus concertStatus;

    public enum ConcertStatus {
        UPCOMING,
        COMPLETED
    }

    @Builder
    public NewConcert(Artist artistId, String title, String subTitle, LocalDate date,
                      String venueName, String cityName, String countryName,
                      String countryCode, String ticketPlatforms, String ticketUrl,
                      String imageUrl, String genre, ConcertStatus concertStatus){
        this.artistId = artistId;
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
        this.venueName = venueName;
        this.cityName = cityName;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.ticketPlatforms = ticketPlatforms;
        this.ticketUrl = ticketUrl;
        this.imageUrl = imageUrl;
        this.genre = genre;
        this.concertStatus = concertStatus;
    }
    // 뉴콘서트 정보 수정 메서드
    public void updateNewConcert(NewConcert newConcert){
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
        this.imageUrl = newConcert.getImageUrl();
        this.genre = newConcert.getGenre();
        this.concertStatus = newConcert.getConcertStatus();
    }
}

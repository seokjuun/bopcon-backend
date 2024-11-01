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
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long concertId;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artistId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "sub_title", length = 100)
    private String subTitle;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "location", nullable = false, length = 100)
    private String location; // 공연장

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "country", nullable = false, length = 50)
    private String country; // ex) Republic of Korea

    @Column(name = "country_code", nullable = false, length = 5)
    private String countryCode; // ex) Kr

    @Column(name = "ticket_url", length = 255)
    private String ticketUrl;
    @Column(name = "image_url", length = 255)
    private String imageUrl; // 포스터
    @Column(name = "genre", length = 50)
    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "concert_type", nullable = false)
    private ConcertType concertType;

    // Concert 엔티티의 내부 enum 으로 정의
    public enum ConcertType {
        PAST,
        NEW
    }

    @Builder
    public Concert(Artist artistId,String title, String subTitle, LocalDate date, String location,
                   String city, String country, String countryCode, String ticketUrl,
                   String imageUrl, String genre, ConcertType concertType){
        this.artistId = artistId;
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
        this.location = location;
        this.city = city;
        this.country = country;
        this.countryCode = countryCode;
        this.ticketUrl = ticketUrl;
        this.imageUrl = imageUrl;
        this.genre = genre;
        this.concertType = concertType;
    }

    // 콘서트 정보 수정 메서드
    public void updateConcert(Concert concert){
        this.artistId = concert.getArtistId();
        this.title = concert.getTitle();
        this.subTitle = concert.getSubTitle();
        this.date = concert.getDate();
        this.location = concert.getLocation();
        this.city = concert.getCity();
        this.country = concert.getCountry();
        this.countryCode = concert.getCountryCode();
        this.ticketUrl = concert.getTicketUrl();
        this.imageUrl = concert.getImageUrl();
        this.genre = concert.getGenre();
        this.concertType = concert.getConcertType();
    }
}

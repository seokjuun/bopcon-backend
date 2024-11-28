package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddNewConcertRequest {

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

    public NewConcert toNewConcert(Artist artist) {
        return NewConcert.builder()
                .artist(artist) // artistId -> artist로 수정
                .title(title)
                .subTitle(subTitle)
                .date(date)
                .venueName(venueName)
                .cityName(cityName)
                .countryName(countryName)
                .countryCode(countryCode)
                .ticketPlatforms(ticketPlatforms)
                .ticketUrl(ticketUrl)
                .posterUrl(posterUrl)
                .genre(genre)
                .concertStatus(concertStatus)
                .build();
    }
}

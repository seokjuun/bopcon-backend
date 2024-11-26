package com.bopcon.backend.dto;


import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateNewConcertRequest {
    private Artist artistId;
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

}

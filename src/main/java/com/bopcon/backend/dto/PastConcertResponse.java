package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.PastConcert;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PastConcertResponse {
    private final Long pastConcertId;  // 콘서트 ID
    private final Long artistId;       // 아티스트 ID
    private final String venueName;    // 공연장 이름
    private final String cityName;     // 도시 이름
    private final String country;      // 국가 이름
    private final LocalDate date;  // 공연 날짜 및 시간



    // 생성자: PastConcert 엔티티를 받아서 DTO로 변환
    public PastConcertResponse(PastConcert pastConcert) {
        this.pastConcertId = pastConcert.getPastConcertId();
        this.artistId = pastConcert.getArtist().getArtistId();
        this.venueName = pastConcert.getVenueName();
        this.cityName = pastConcert.getCityName();
        this.country = pastConcert.getCountry();
        this.date = pastConcert.getDate();
    }
}

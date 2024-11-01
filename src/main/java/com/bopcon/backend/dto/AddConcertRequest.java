package com.bopcon.backend.dto;


import com.bopcon.backend.domain.Concert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddConcertRequest {
    private String title;
    private String subTitle;
    private LocalDate date;
    private String location; // 공연장
    private String city;
    private String country; // ex) Republic of Korea
    private String countryCode; // ex) Kr
    private String ticketUrl;
    private String imageUrl; // 포스터
    private String genre;
    private Concert.ConcertType concertType;

    public Concert toEntity(){
        return Concert.builder()
                .title(title)
                .subTitle(subTitle)
                .date(date)
                .location(location)
                .city(city)
                .country(country)
                .countryCode(countryCode)
                .ticketUrl(ticketUrl)
                .imageUrl(imageUrl)
                .genre(genre)
                .concertType(concertType)
                .build();
    }
}

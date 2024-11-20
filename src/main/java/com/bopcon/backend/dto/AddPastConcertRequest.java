package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.PastConcert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자
@Getter
public class AddPastConcertRequest {
    private Long artistId;        // 아티스트 ID
    private String venueName;     // 공연장 이름
    private String cityName;      // 도시 이름
    private String country;       // 국가 이름
    private LocalDateTime date;   // 공연 날짜 및 시간

    // Request DTO에서 엔티티로 변환하는 메서드
    public PastConcert toPastConcert(Artist artist) {
        return PastConcert.builder()
                .artistId(artist)
                .venueName(venueName)
                .cityName(cityName)
                .country(country)
                .date(date)
                .build();
    }
}

package com.bopcon.backend.dto;

import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.dto.SetlistDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON에서 제외
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PastConcertDTO {
    private Long pastConcertId;
    private String venueName;
    private String cityName;
    private String country;
    private LocalDate date;
    private List<SetlistDTO> setlists;

    // 정적 팩토리 메서드: 엔티티 -> DTO 변환
    public static PastConcertDTO fromEntity(PastConcert pastConcert) {
        return new PastConcertDTO(
                pastConcert.getPastConcertId(),
                pastConcert.getVenueName(),
                pastConcert.getCityName(),
                pastConcert.getCountry(),
                pastConcert.getDate(),
                pastConcert.getSetlists().stream()
                        .map(SetlistDTO::fromEntity) // ConcertSetlist -> DTO 변환
                        .collect(Collectors.toList())
        );
    }
}

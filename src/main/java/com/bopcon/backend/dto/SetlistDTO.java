package com.bopcon.backend.dto;

import com.bopcon.backend.domain.ConcertSetlist;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetlistDTO {
    private Integer order;
    private Long concertSetlistId;
    private Long concertId;
    private String concertType; // "past" 또는 "new"로 구분
    private SongDTO song;

    public static SetlistDTO fromEntity(ConcertSetlist concertSetlist){
        return new SetlistDTO(
                concertSetlist.getOrder(),
                concertSetlist.getConcertSetlistId(),
                concertSetlist.getPastConcert() != null
                    ? concertSetlist.getPastConcert().getPastConcertId()
                        : concertSetlist.getNewConcert().getNewConcertId(),
                concertSetlist.getPastConcert() != null ? "past" : "new",
                SongDTO.fromEntity(concertSetlist.getSong())
        );
    }
}

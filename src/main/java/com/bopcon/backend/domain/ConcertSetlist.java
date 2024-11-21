package com.bopcon.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSetlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_setlist_id", nullable = false, updatable = false)
    private Long concertSetlistId;

    @ManyToOne
    @JoinColumn(name = "newconcert_id", nullable = true) // Nullable로 설정
    private NewConcert newConcert; // NewConcert와 연관 관계

    @ManyToOne
    @JoinColumn(name = "pastconcert_id", nullable = true) // Nullable로 설정
    private PastConcert pastConcert; // PastConcert와 연관 관계

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song songId; // Song과 연관 관계

    @Column(name = "`order`", nullable = false)
    private Integer order; // 곡 순서

    // 뉴 콘서트와 과거 콘서트 중 하나는 null 값이어야 함
    @Builder
    public ConcertSetlist(NewConcert newConcert, PastConcert pastConcert, Song song, Integer order) {
        if ((newConcert != null && pastConcert != null) || (newConcert == null && pastConcert == null)) {
            throw new IllegalArgumentException("새 콘서트와 과거 콘서트 중 하나는 null 값이어야 함");
        }
        this.newConcert = newConcert;
        this.pastConcert = pastConcert;
        this.songId = song;
        this.order = order;
    }
}

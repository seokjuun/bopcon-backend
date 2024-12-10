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
    @Column(name = "concert_setlist_id", updatable = false)
    private Long concertSetlistId;

    @ManyToOne
    @JoinColumn(name = "newconcert_id", nullable = true) // Nullable로 설정
    private NewConcert newConcert; // NewConcert와 연관 관계

    @ManyToOne
    @JoinColumn(name = "pastconcert_id", nullable = true) // Nullable로 설정
    private PastConcert pastConcert; // PastConcert와 연관 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song; // Song과 연관 관계

    @Column(name = "`order`", nullable = false)
    private Integer order; // 곡 순서

    @Builder
    public ConcertSetlist(PastConcert pastConcert, Song song, Integer order) {
        this.pastConcert = pastConcert;
        this.song = song;
        this.order = order;
    }
}

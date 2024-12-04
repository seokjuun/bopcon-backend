package com.bopcon.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PredictSetlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "new_concert_id", nullable = false)
    private NewConcert newConcert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    @Builder
    public PredictSetlist(NewConcert newConcert, Song song, Integer order) {
        this.newConcert = newConcert;
        this.song = song;
        this.order = order;
    }
}
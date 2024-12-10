package com.bopcon.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id", updatable = false)
    private Long songId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false) // @Column 대신 @JoinColumn 사용
    private Artist artist; // 아티스트 ID

    @Column(name = "title", nullable = false, length = 255)
    private String title; // 곡 제목

    @Column(name = "lyrics", columnDefinition = "TEXT")
    private String lyrics;

    @Column(name = "yt_link", length = 255)
    private String ytLink; // YouTube 링크

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ConcertSetlist> concertSetlists = new ArrayList<>();

    @Builder
    public Song(Artist artist, String title, String lyrics, String ytLink) {
        this.artist = artist;
        this.title = title;
        this.lyrics = lyrics;
        this.ytLink = ytLink;
    }
}

package com.bopcon.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id", nullable = false, updatable = false)
    private Long songId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false) // @Column 대신 @JoinColumn 사용
    @JsonIgnore // 순환 참조 방지
    private Artist artistId; // 아티스트 ID

    @Column(name = "title", nullable = false, length = 255)
    private String title; // 곡 제목

    @Column(name = "count")
    private Integer count; // 재생 횟수 (또는 셋리스트에서 등장 횟수)

    @Column(name = "yt_link", length = 255)
    private String ytLink; // YouTube 링크

    @Builder
    public Song(Artist artistId, String title, Integer count, String ytLink) {
        this.artistId = artistId;
        this.title = title;
        this.count = count;
        this.ytLink = ytLink;
    }

    // 재생 횟수 증가 메서드
    public void incrementCount() {
        if (this.count == null) {
            this.count = 1;
        } else {
            this.count++;
        }
    }

    // YouTube 링크 업데이트 메서드
    public void updateYouTubeLink(String ytLink) {
        this.ytLink = ytLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(songId, song.songId) &&
                Objects.equals(title, song.title) &&
                Objects.equals(artistId, song.artistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songId, title, artistId);
    }


}

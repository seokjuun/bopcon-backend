package com.bopcon.backend.domain;

import com.bopcon.backend.dto.UpdateArtistRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class) // 엔티티의 생성 및 수정 시간을 자동으로 감시하고 기록
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id", updatable = false)
    private Long artistId;

    @Column(name = "mbid", nullable = false)
    private String mbid;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "sns_url")
    private String snsUrl;
    @Column(name = "media_url")
    private String mediaUrl;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    // PastConcert와의 연관 관계 추가
    @OneToMany(mappedBy = "artistId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PastConcert> pastConcerts = new ArrayList<>();

    // Song과의 연관 관계 추가
    @OneToMany(mappedBy = "artistId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> Song = new ArrayList<>();


    @Builder // 빌더 패턴으로 객체 생성
    public Artist(String mbid, String name, String imgUrl, String snsUrl, String mediaUrl) {
        this.mbid = mbid;
        this.name = name;
        this.imgUrl = imgUrl;
        this.snsUrl = snsUrl;
        this.mediaUrl = mediaUrl;
    }

    // 수정 메서드
    public void updateArtist(UpdateArtistRequest artist) {
        this.mbid = artist.getMbid();
        this.name = artist.getName();
        this.imgUrl = artist.getImgUrl();
        this.snsUrl = artist.getSnsUrl();
        this.mediaUrl = artist.getMediaUrl();
    }
}

package com.bopcon.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class) // 엔티티의 생성 및 수정 시간을 자동으로 기록
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PastConcert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pastconcert_id", updatable = false, nullable = false)
    private Long pastConcertId; // 과거 콘서트 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist; // 아티스트 ID (Many-to-One 관계)

    @Column(name = "venue_name", nullable = false, length = 100)
    private String venueName; // 공연장 이름

    @Column(name = "city_name", nullable = false, length = 50)
    private String cityName; // 도시 이름

    @Column(name = "country", nullable = false, length = 50)
    private String country; // 국가 이름

    @Column(name = "date", nullable = false)
    private LocalDate date; // 공연 날짜 및 시간

    @OneToMany(mappedBy = "pastConcert", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ConcertSetlist> setlists = new ArrayList<>();

    @Builder
    public PastConcert(Artist artist, String venueName, String cityName, String country, LocalDate date) {
        this.artist = artist;
        this.venueName = venueName;
        this.cityName = cityName;
        this.country = country;
        this.date = date;
    }
}

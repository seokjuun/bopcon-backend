package com.bopcon.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;


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
    @JsonBackReference // 순환 참조 방지
    private Artist artistId; // 아티스트 ID (Many-to-One 관계)

    @Column(name = "venue_name", nullable = false, length = 100)
    private String venueName; // 공연장 이름

    @Column(name = "city_name", nullable = false, length = 50)
    private String cityName; // 도시 이름

    @Column(name = "country", nullable = false, length = 50)
    private String country; // 국가 이름

    @Column(name = "date", nullable = false)
    private LocalDateTime date; // 공연 날짜 및 시간

    @Builder
    public PastConcert(Artist artistId, String venueName, String cityName, String country, LocalDateTime date) {
        this.artistId = artistId;
        this.venueName = venueName;
        this.cityName = cityName;
        this.country = country;
        this.date = date;
    }

    // PastConcert 정보 수정 메서드
    public void updatePastConcert(String venueName, String cityName, String country, LocalDateTime date) {
        this.venueName = venueName;
        this.cityName = cityName;
        this.country = country;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PastConcert that = (PastConcert) o;
        return Objects.equals(pastConcertId, that.pastConcertId) &&
                Objects.equals(date, that.date) &&
                Objects.equals(venueName, that.venueName) &&
                Objects.equals(cityName, that.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pastConcertId, date, venueName, cityName);
    }

}

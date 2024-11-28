package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewConcertRepository extends JpaRepository<NewConcert, Long> {

    // 🔥 특정 아티스트의 콘서트 검색 (Artist ID 사용)
    List<NewConcert> findByArtist_ArtistId(Long artistId);

    // 🔥 키워드로 콘서트 검색 (제목, 공연장 이름, 장르)
    List<NewConcert> findByTitleContainingIgnoreCaseOrVenueNameContainingIgnoreCaseOrGenreContainingIgnoreCase(
            String title, String venueName, String genre);

    // 🔍 특정 장르 검색 (대소문자 무시)
    List<NewConcert> findByGenreContainingIgnoreCase(String genre);

    // 🔥 특정 아티스트 ID와 상태로 콘서트 검색
    List<NewConcert> findByArtist_ArtistIdAndConcertStatus(Long artistId, NewConcert.ConcertStatus concertStatus);

    // 🔍 특정 Artist 객체로 검색
    List<NewConcert> findByArtist(Artist artist);
}

package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewConcertRepository extends JpaRepository<NewConcert, Long> {

    // 키워드로 타이틀, 공연장 이름, 장르 검색
    List<NewConcert> findByTitleContainingIgnoreCaseOrVenueNameContainingIgnoreCaseOrGenreContainingIgnoreCase(
            String title, String venueName, String genre);
    // 🔍 장르별로 검색
    List<NewConcert> findByGenreContainingIgnoreCase(String genre);
}

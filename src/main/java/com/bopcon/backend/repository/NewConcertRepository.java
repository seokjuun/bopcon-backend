package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewConcertRepository extends JpaRepository<NewConcert, Long> {
    List<NewConcert> findByGenre(String genre);

    List<NewConcert> findByArtist(Artist artist);

    List<NewConcert> findAll(); // 전체 콘서트를 가져오는 기본 메서드
}

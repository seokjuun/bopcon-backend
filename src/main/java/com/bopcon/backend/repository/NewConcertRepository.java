package com.bopcon.backend.repository;

import com.bopcon.backend.domain.NewConcert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewConcertRepository extends JpaRepository<NewConcert, Long> {
    // 🔥 Genre에 해당하는 콘서트 검색
    List<NewConcert> findByGenreContainingIgnoreCase(String genre);

    // 🔥 Title에 특정 문자열이 포함된 콘서트 검색
    List<NewConcert> findByTitleContainingIgnoreCase(String title);

    // 상태로 검색
    List<NewConcert> findByConcertStatus(NewConcert.ConcertStatus status);

    // 🔥 도시 이름에 특정 문자열이 포함된 콘서트 검색
    List<NewConcert> findByCityNameContainingIgnoreCase(String cityName);
}

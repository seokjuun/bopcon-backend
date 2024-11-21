package com.bopcon.backend.repository;


import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.PastConcert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PastConcertRepository extends JpaRepository<PastConcert, Long> {

    // 특정 날짜, 공연장, 도시를 기준으로 중복 데이터를 확인하기 위한 메서드
    Optional<PastConcert> findByDateAndVenueNameAndCityName(LocalDateTime date, String venueName, String cityName);


    // artist 엔티티의 mbid 필드 기준으로 검색
    List<PastConcert> findByArtistId_Mbid(String mbid);

    List<PastConcert> findByArtistId(Artist artistId);

    List<PastConcert> findByArtistId_Name(String name);




}
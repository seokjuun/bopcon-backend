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

    // artist 엔티티의 name 필드 기준으로 검색
    List<PastConcert> findByArtistId_Name(String name);

//    // 날짜와 장소로 중복된 콘서트 확인
//    boolean existsByDateAndVenueNameAndCityName(String date, String venueName, String cityName);

    List<PastConcert> findByArtistId_ArtistId(Long artistId);


}
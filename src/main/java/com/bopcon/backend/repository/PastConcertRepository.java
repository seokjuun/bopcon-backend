package com.bopcon.backend.repository;


import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.dto.PastConcertSetlistDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PastConcertRepository extends JpaRepository<PastConcert, Long> {
    List<PastConcert> findAllByArtist_ArtistId(Long artistId);

    // 날짜와 셋리스트 정보 가져오기
    @Query("SELECT new com.bopcon.backend.dto.PastConcertSetlistDTO(pc.date, cs.song.title, cs.order) " +
            "FROM PastConcert pc " +
            "JOIN pc.setlists cs " +
            "WHERE pc.artist.artistId = :artistId " +
            "ORDER BY pc.date ASC, cs.order ASC")
    List<PastConcertSetlistDTO> findConcertSetlistsByArtistId(@Param("artistId") Long artistId);
}
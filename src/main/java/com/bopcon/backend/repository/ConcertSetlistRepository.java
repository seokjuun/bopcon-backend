package com.bopcon.backend.repository;

import com.bopcon.backend.domain.ConcertSetlist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.dto.SongRankingDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Past;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertSetlistRepository extends JpaRepository<ConcertSetlist, Long> {
        List<ConcertSetlist> findAllByPastConcert_PastConcertId(Long pastConcertId); // 특정 콘서트의 셋리스트 가져오기

        @Query("SELECT new com.bopcon.backend.dto.SongRankingDTO(s.songId, s.title, COUNT(cs)) " +
                "FROM ConcertSetlist cs " +
                "JOIN cs.song s " +
                "WHERE s.artist.artistId = :artistId " +
                "GROUP BY s.songId, s.title " +
                "ORDER BY COUNT(cs) DESC")
        List<SongRankingDTO> findSongRankingByArtistId(@Param("artistId") Long artistId);
}




package com.bopcon.backend.repository;

import com.bopcon.backend.domain.ConcertSetlist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertSetlistRepository extends JpaRepository<ConcertSetlist, Long> {

    // 특정 newConcert ID에 해당하는 셋리스트를 순서(order)대로 조회
//    List<ConcertSetlist> findByNewConcertnewConcertOrderByOrder(Long newConcertId);

    // 특정 pastConcert ID에 해당하는 셋리스트를 순서(order)대로 조회
//    List<ConcertSetlist> findByPastConcertpastConcertOrderByOrder(Long pastConcertId);

//    boolean existsByPastConcertAndSong(PastConcert pastConcert, Song song);
//
//    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN TRUE ELSE FALSE END " +
//            "FROM ConcertSetlist cs " +
//            "WHERE cs.pastConcert = :pastConcert AND cs.songId = :song")
//    boolean existsByPastConcertAndSong(@Param("pastConcert") PastConcert pastConcert,
//                                       @Param("song") Song songId);

    @Query("SELECT cs.songId.title FROM ConcertSetlist cs WHERE cs.pastConcert.pastConcertId = :pastConcertId")
    List<String> findSongTitlesByPastConcertId(@Param("pastConcertId") Long pastConcertId);


}
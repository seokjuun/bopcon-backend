package com.bopcon.backend.repository;

import com.bopcon.backend.domain.ConcertSetlist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import jakarta.validation.constraints.Past;
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



        // 특정 PastConcert ID에 해당하는 곡 제목 리스트 조회
        @Query("SELECT cs.songId.title FROM ConcertSetlist cs WHERE cs.pastConcert.pastConcertId = :pastConcertId")
        List<String> findSongTitlesByPastConcertId(@Param("pastConcertId") Long pastConcertId);

        // 특정 PastConcert ID에 해당하는 셋리스트를 순서대로 조회
        List<ConcertSetlist> findByPastConcert_PastConcertIdOrderByOrder(Long pastConcertId);

        // 특정 PastConcert와 Song 간의 관계가 존재하는지 확인
        boolean existsByPastConcertAndSongId(PastConcert pastConcert, Song songId);

        // 특정 PastConcert에 포함된 모든 셋리스트 반환
        List<ConcertSetlist> findAllByPastConcert(PastConcert pastConcert);

        // 특정 아티스트 ID로 모든 곡 제목 가져오기 (중복 제거)
        @Query("SELECT DISTINCT cs.songId.title FROM ConcertSetlist cs WHERE cs.pastConcert.artistId.artistId = :artistId")
        List<String> findAllSongTitlesByArtistId(@Param("artistId") Long artistId);

        // 특정 아티스트 ID로 모든 셋리스트 데이터 가져오기
        @Query("SELECT cs FROM ConcertSetlist cs WHERE cs.pastConcert.artistId.artistId = :artistId ORDER BY cs.order ASC")
        List<ConcertSetlist> findAllByArtistId(@Param("artistId") Long artistId);
    }




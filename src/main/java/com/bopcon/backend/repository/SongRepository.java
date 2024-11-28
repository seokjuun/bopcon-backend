package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {


    // 특정 Song 제목으로 Song 조회
    Optional<Song> findByTitle(String title); // 아티스트 ID와 곡 제목으로 검색



    List<Song> findAllByArtistId(Artist artistId);

    Optional<Song> findFirstByTitleAndArtistId(String title, Artist artistId);

    // artistId를 기반으로 Song 개수 조회
    long countByArtistId(Artist artistId);

    // artistId를 기반으로 Song 리스트 조회
    List<Song> findByArtistId(Artist artistId);

    // 특정 title과 artistId로 Song 검색
    Optional<Song> findByTitleAndArtistId(String title, Artist artistId);







    // 아티스트 ID 기준으로 곡을 카운트 순으로 내림차순 정렬
    @Query("SELECT s FROM Song s WHERE s.artistId.artistId = :artistId ORDER BY s.count DESC")
    List<Song> findTopSongsByArtistId(@Param("artistId") Long artistId);

    @Query("SELECT s FROM Song s WHERE s.artistId.artistId = :artistId AND s.title IN :titles")
    List<Song> findAllByArtistIdAndTitleIn(@Param("artistId") Long artistId, @Param("titles") List<String> titles);

}

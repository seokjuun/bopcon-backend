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
    Optional<Song> findByTitle(String title); // 아티스트 ID와 곡 제목으로 검색

    Optional<Song> findByTitleAndArtistId(String title, Artist artistId); //곡 제목과 아티스트로 Song을 조회하는 메서드

    // 아티스트 이름과 곡 제목으로 노래 조회
    Optional<Song> findByArtistIdAndTitle(Artist artist, String title);

    List<Song> findAllByArtistId(Artist artistId);




    // 아티스트 ID 기준으로 곡을 카운트 순으로 내림차순 정렬
    @Query("SELECT s FROM Song s WHERE s.artistId.artistId = :artistId ORDER BY s.count DESC")
    List<Song> findTopSongsByArtistId(@Param("artistId") Long artistId);
}

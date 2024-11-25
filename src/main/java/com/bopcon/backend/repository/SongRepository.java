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

    @Query("SELECT s FROM Song s WHERE s.title = :title AND s.artistId.artistId = :artistId")
    Optional<Song> findByTitleAndArtistId(@Param("title") String title, @Param("artistId") Long artistId);
    // 아티스트 이름과 곡 제목으로 노래 조회
    Optional<Song> findByArtistIdAndTitle(Artist artist, String title);

    // 새로운 메서드 - 숫자 '1'을 포함 (Artist ID를 Long 타입으로 사용)
    @Query("SELECT s FROM Song s WHERE s.artistId.artistId = :artistId AND s.title = :title")
    Optional<Song> findByArtistIdAndTitle1(@Param("artistId") Long artistId, @Param("title") String title);

    List<Song> findAllByArtistId(Artist artistId);




    // 아티스트 ID 기준으로 곡을 카운트 순으로 내림차순 정렬
    @Query("SELECT s FROM Song s WHERE s.artistId.artistId = :artistId ORDER BY s.count DESC")
    List<Song> findTopSongsByArtistId(@Param("artistId") Long artistId);
}

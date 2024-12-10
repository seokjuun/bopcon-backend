package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findByTitleAndArtist_ArtistId(String title, Long artistId); // 특정 아티스트의 곡 검색
}

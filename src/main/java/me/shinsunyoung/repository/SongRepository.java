package me.shinsunyoung.repository;

import me.shinsunyoung.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    // 특정 아티스트 이름으로 노래 검색
    List<Song> findByArtistName(String artistName);

    // 특정 아티스트 이름과 제목으로 중복 여부 확인
    boolean existsByArtistNameAndTitle(String artistName, String title);

    Optional<Song> findByTitleAndArtistName(String title, String artistName);
}

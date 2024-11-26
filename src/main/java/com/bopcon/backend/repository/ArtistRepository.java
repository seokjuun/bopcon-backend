package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    // 🔥 키워드로 아티스트 검색 (영문/한글 이름)
    Optional<Artist> findByNameContainingIgnoreCaseOrKrNameContainingIgnoreCase(String name, String krName);
    //mbid로 아티스트를 조회할 수 있도록 메서드
    Optional<Artist> findByMbid(String mbid);

    Optional<Artist> findByName(String name);

    Optional<Artist> findById(Long id);
}

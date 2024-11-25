package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    // 🔥 키워드로 아티스트 검색 (영문/한글 이름)
    Optional<Artist> findByNameContainingIgnoreCaseOrKrNameContainingIgnoreCase(String name, String krName);
}

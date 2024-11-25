package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    // 이름(name) 또는 한글 이름(krName)으로 검색
    Optional<Artist> findByNameContainingIgnoreCaseOrKrNameContainingIgnoreCase(String name, String krName);
}
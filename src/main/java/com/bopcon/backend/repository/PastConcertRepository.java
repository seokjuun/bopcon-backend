package com.bopcon.backend.repository;


import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.PastConcert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PastConcertRepository extends JpaRepository<PastConcert, Long> {
    List<PastConcert> findAllByArtist_ArtistId(Long artistId);
}
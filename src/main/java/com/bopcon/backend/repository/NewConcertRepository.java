package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewConcertRepository extends JpaRepository<NewConcert, Long> {
    List<NewConcert> findByGenre(String genre);

    List<NewConcert> findByArtistId_ArtistIdAndConcertStatus(Long artistId, NewConcert.ConcertStatus concertStatus);

}

package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Favorite;
import com.bopcon.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUser(User user);

    Optional<Favorite> findByUserAndArtistArtistId(User user, Long artistId);
    Optional<Favorite> findByUserAndNewConcertNewConcertId(User user, Long concertId);

}

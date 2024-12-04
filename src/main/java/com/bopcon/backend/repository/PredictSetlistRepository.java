package com.bopcon.backend.repository;

import com.bopcon.backend.domain.PredictSetlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictSetlistRepository extends JpaRepository<PredictSetlist, Long> {
    List<PredictSetlist> findAllByNewConcert_NewConcertId(Long newConcertId);
}
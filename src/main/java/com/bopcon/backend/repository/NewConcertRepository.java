package com.bopcon.backend.repository;

import com.bopcon.backend.domain.NewConcert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewConcertRepository extends JpaRepository<NewConcert, Long> {
}

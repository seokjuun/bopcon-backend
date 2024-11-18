package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}

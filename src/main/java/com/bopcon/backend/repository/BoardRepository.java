package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Article, Long> {
}

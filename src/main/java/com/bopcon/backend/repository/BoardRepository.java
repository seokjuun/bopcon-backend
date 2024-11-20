package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Article, Long> {
    List<Article> findByArtistArtistId(Long artistId);
}

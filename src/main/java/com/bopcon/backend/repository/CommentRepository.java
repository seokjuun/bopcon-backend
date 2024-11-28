package com.bopcon.backend.repository;

import com.bopcon.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleId(Long articleId); // 게시글 ID로 댓글 목록 조회
    List<Comment> findByUserId(Long userId); // 특정 유저의 댓글 목록 조회
}
package com.bopcon.backend.service;

import com.bopcon.backend.domain.Article;
import com.bopcon.backend.domain.Comment;
import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.AddCommentRequest;
import com.bopcon.backend.dto.CommentResponse;
import com.bopcon.backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardService boardService;
    private final UserService userService;

    // 댓글 추가
    public Comment addComment(AddCommentRequest request, User user) {
        Article article = boardService.findById(request.getArticleId()); // 게시글 조회

        Comment comment = Comment.builder()
                .article(article)
                .user(user) // 인증 유저
                .content(request.getContent())
                .build();

        article.updateCommentCount(1);
        return commentRepository.save(comment);
    }

    // 게시글의 댓글 목록 조회
    public List<CommentResponse> findCommentsByArticle(Long articleId) {
        return commentRepository.findByArticleId(articleId)
                .stream()
                .map(CommentResponse::new)
                .toList();
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You do not have permission to delete this comment.");
        }

        comment.getArticle().updateCommentCount(-1); // 댓글 수 감소
        commentRepository.deleteById(commentId);
    }
}
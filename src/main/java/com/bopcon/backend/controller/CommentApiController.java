package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Comment;
import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.AddCommentRequest;
import com.bopcon.backend.dto.CommentResponse;
import com.bopcon.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    // 댓글 추가
    @PostMapping("/api/comments")
    public ResponseEntity<CommentResponse> addComment(
            @RequestBody AddCommentRequest request,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }

        Comment comment = commentService.addComment(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentResponse(comment));
    }

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/api/comments/article/{articleId}")
    public ResponseEntity<List<CommentResponse>> findComments(@PathVariable Long articleId) {
        List<CommentResponse> comments = commentService.findCommentsByArticle(articleId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }

        commentService.deleteComment(id, user);
        return ResponseEntity.ok().build();
    }

    // 댓글 수정

    // 특정 유저 댓글 목록 조회 (마이페이지)
}

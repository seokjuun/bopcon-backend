package com.bopcon.backend.dto;

import lombok.Getter;

@Getter

public class AddCommentRequest {
    private Long articleId; // 속한 게시글 ID
    private Long userId;    // 작성자 ID
    private String content;
}

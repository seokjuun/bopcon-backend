package com.bopcon.backend.dto;

import lombok.Getter;
import com.bopcon.backend.domain.Board;

@Getter
public class BoardResponse {
    private final String title;
    private final String content;
    // 엔티티를 인수로 받는 생성자
    public BoardResponse(Board board) {
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}

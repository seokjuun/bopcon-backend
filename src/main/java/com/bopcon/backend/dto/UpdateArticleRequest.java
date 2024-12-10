package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateArticleRequest {
    private String title;
    private String content;
    private String categoryType;
    private Long newConcertId;

    // 필요시 유효성 검사를 위한 메서드 추가 가능
    public boolean isValid() {
        return title != null && !title.trim().isEmpty()
                && content != null && !content.trim().isEmpty();
    }
}

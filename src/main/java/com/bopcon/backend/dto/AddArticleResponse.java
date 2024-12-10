package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Article;
import lombok.Getter;

@Getter
public class AddArticleResponse {
    private final Long id; // 생성된 글 ID
    private final String title; // 제목
    private final String content; // 내용
    private final String categoryType; // 카테고리 타입
    private final String artistName; // 아티스트 이름
    private final String nickname; // 작성자 이름

    public AddArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.categoryType = article.getCategoryType().name();
        this.artistName = article.getArtist().getName();
        this.nickname = article.getUser().getNickname();
    }
}
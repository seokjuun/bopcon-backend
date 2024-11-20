package com.bopcon.backend.dto;

import lombok.Getter;
import com.bopcon.backend.domain.Article;

@Getter
public class ArticleResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String categoryType;
    private final String artistName; // 아티스트 이름
    private final String userName; // 작성자 이름
    private final String concertTitle; // 콘서트 제목 (카테고리가 콘서트일 경우)
    private final Integer likeCount; // 좋아요 수
    private final Integer commentCount; // 댓글 수

    // 엔티티를 인수로 받는 생성자
    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.categoryType = article.getCategoryType().name(); // ENUM -> 문자열
        this.artistName = article.getArtist().getName(); // 아티스트 이름
        this.userName = article.getUser().getNickname(); // 작성자 닉네임
        this.concertTitle = article.getNewConcert() != null ? article.getNewConcert().getTitle() : null; // 콘서트 제목
        this.likeCount = article.getLikeCount();
        this.commentCount = article.getCommentCount();
    }
}

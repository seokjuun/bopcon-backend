package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Article;
import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {
    private String title;
    private String content;
    private Article.CategoryType categoryType; // 게시글 유형 (자유게시판 or 콘서트)
    private Long artistId; // 아티스트 ID
    private Long newConcertId; // 콘서트 ID (카테고리가 NEW_CONCERT일 때만 필요)

    public Article toEntity(Artist artist, User user, NewConcert newConcert) {
        // 생성자를 사용해 Article 객체 생성 반환
        return Article.builder()
                .artist(artist) // 연관된 Artist 객체
                .user(user) // 연관된 User 객체
                .title(title)
                .content(content)
                .categoryType(categoryType) // 카테고리 유형 설정
                .newConcert(newConcert) // 연관된 콘서트 객체 (null 가능)
                .likeCount(0) // 기본 값 설정
                .commentCount(0) // 기본 값 설정
                .build();
    }
}

package com.bopcon.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class) // 엔티티의 생성 및 수정 시간을 자동으로 감시하고 기록
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id // id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "category_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    // 카테고리가 콘서트를 참조할 수 있음
    @ManyToOne
    @JoinColumn(name = "new_concert_id")
    private NewConcert newConcert;

    @ColumnDefault("0")
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @ColumnDefault("0")
    @Column(name = "comment_count",nullable = false)
    private Integer commentCount;

    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder // 빌더 패턴으로 객체 생성
    public Article(Artist artist, User user, String title, String content, CategoryType categoryType,NewConcert newConcert ,Integer likeCount, Integer commentCount) {
        this.artist = artist;
        this.user = user;
        this.title = title;
        this.content = content;
        this.categoryType = categoryType;
        this.newConcert = newConcert;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public void update(String title, String content, CategoryType categoryType, NewConcert newConcert) {
        this.title = title;
        this.content = content;
        this.categoryType = categoryType;
        this.newConcert = newConcert;
    }

    public enum CategoryType {
        FREE_BOARD, // 자유게시판
        NEW_CONCERT     // 콘서트 게시판
    }

    // 댓글 수 업데이트
    public void updateCommentCount(int count) {
        this.commentCount += count;
    }
}

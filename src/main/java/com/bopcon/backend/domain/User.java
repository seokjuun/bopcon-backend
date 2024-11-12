package com.bopcon.backend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성 및 수정 시간 자동 기록
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long id;

    @Column(nullable = false, unique = true) // 유일한 이메일
    private String email;

    @Column(nullable = false, unique = true) // 유일한 닉네임
    private String nickname;

    @Column(nullable = false) // 필수 비밀번호
    private String password;

    @CreatedDate // 생성일 자동 기록
    @Column(updatable = false) // 생성 후 변경 불가
    private LocalDateTime createdAt;

    @LastModifiedDate // 마지막 수정일 자동 기록
    private LocalDateTime updatedAt;

    @Builder // 빌더 패턴으로 객체 생성
    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }
}
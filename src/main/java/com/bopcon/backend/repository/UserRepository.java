package com.bopcon.backend.repository;

import com.bopcon.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname); // 닉네임으로 사용자 찾기
    boolean existsByEmail(String email); // 이메일 존재 여부 확인
    boolean existsByNickname(String nickname); // 닉네임 존재 여부 확인
}

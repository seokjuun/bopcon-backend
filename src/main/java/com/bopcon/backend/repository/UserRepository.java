package com.bopcon.backend.repository;

import com.bopcon.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일을 사용해 사용자 조회
    Optional<User> findByEmail(String email);

    // 닉네임을 사용해 사용자 조회
    Optional<User> findByNickname(String nickname);
}
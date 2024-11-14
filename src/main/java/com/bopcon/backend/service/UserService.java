package com.bopcon.backend.service;

import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.AddUserRequest;
import com.bopcon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
        // 비밀번호 확인과 비밀번호가 일치하는지 검증
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // User 엔티티에 이메일, 암호화된 비밀번호, 닉네임 저장
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .build()).getId();
    }
}

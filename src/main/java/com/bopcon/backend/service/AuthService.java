package com.bopcon.backend.service;

import com.bopcon.backend.config.jwt.TokenProvider;
import com.bopcon.backend.domain.RefreshToken;
import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.LoginRequest;
import com.bopcon.backend.dto.LoginResponse;
import com.bopcon.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // 사용자 인증
        User user = userService.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호입니다.");
        }

        // JWT 토큰 생성
        String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2));
        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(14));

        // 리프레시 토큰 저장
        RefreshToken token = refreshTokenRepository.findByUserId(user.getId())
                .orElse(new RefreshToken(user.getId(), refreshToken));
        token.update(refreshToken);
        refreshTokenRepository.save(token);

//        return new LoginResponse(accessToken, refreshToken);
        return new LoginResponse(accessToken, refreshToken, user.getNickname());
    }
}

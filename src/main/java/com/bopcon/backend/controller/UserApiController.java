package com.bopcon.backend.controller;

import com.bopcon.backend.dto.AddUserRequest;
import com.bopcon.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    // 회원가입 엔드포인트
    @PostMapping("/api/auth/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody AddUserRequest request) {
        userService.save(request); // 회원 가입 메서드 호출
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다.");
    }

    // 로그아웃 엔드포인트 (JWT 환경)
    @PostMapping("/api/auth/logout")
    public ResponseEntity<String> logout() {
        // 클라이언트에서 JWT 토큰 삭제를 처리하고, 서버는 로그아웃 성공 응답만 반환
        return ResponseEntity.ok("로그아웃이 성공적으로 처리되었습니다.");
    }
}

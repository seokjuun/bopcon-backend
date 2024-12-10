package com.bopcon.backend.controller;

import com.bopcon.backend.dto.AddUserRequest;
import com.bopcon.backend.dto.LoginRequest;
import com.bopcon.backend.dto.LoginResponse;
import com.bopcon.backend.service.AuthService;
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
    private final AuthService authService;

    // 회원가입 엔드포인트
    @PostMapping("/api/auth/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody AddUserRequest request) {
        userService.save(request); // 회원 가입 메서드 호출
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 성공적으로 완료되었습니다.");
    }

    // 로그인 엔드포인트
    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request); // 로그인 처리
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

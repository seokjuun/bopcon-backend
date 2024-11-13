package com.bopcon.backend.controller;

import com.bopcon.backend.dto.TokenResponse;
import com.bopcon.backend.service.UserService;
import com.bopcon.backend.dto.LoginRequest;
import com.bopcon.backend.dto.SignUpRequest;
import com.bopcon.backend.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 API
    @PostMapping("/auth/signup")
    public ResponseEntity<String> register(@Valid @RequestBody SignUpRequest request) {
        try {
            String message = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 로그인 API
    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            TokenResponse tokenResponse = userService.login(request);
            return ResponseEntity.ok(tokenResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}


// 클라이언트에서 로그아웃

// 로그아웃 함수 정의
//function logout() {
//    // 로컬 스토리지에서 토큰 삭제
//    localStorage.removeItem("accessToken");
//    localStorage.removeItem("refreshToken");
//
//    // 세션 스토리지를 사용하는 경우에도 동일하게 삭제
//    // sessionStorage.removeItem("accessToken");
//    // sessionStorage.removeItem("refreshToken");
//
//    // 로그아웃 후 리다이렉트
//    window.location.href = "/login";  // 로그인 페이지로 리다이렉트
//}
//
//// 로그아웃 버튼 클릭 시 함수 호출
//document.getElementById("logoutButton").addEventListener("click", logout);

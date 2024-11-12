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
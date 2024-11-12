package com.bopcon.backend.controller;


import com.bopcon.backend.dto.LoginRequest;
import com.bopcon.backend.dto.SignUpRequest;
import com.bopcon.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll(); // 각 테스트 전에 데이터베이스 초기화
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
    public void registerSuccess() throws Exception {
        // given: 회원가입 요청 데이터 생성
        SignUpRequest signupRequest = new SignUpRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setNickname("testuser");
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");

        // when: 회원가입 요청 수행
        ResultActions result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        // then: 응답 상태가 201 Created인지 확인
        result.andExpect(status().isCreated());
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    public void loginSuccess() throws Exception {
        // given: 회원가입 후 로그인 요청 데이터 생성
        SignUpRequest signupRequest = new SignUpRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setNickname("testuser");
        signupRequest.setPassword("password123");
        signupRequest.setConfirmPassword("password123");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // when: 로그인 요청 수행
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // then: 응답 상태가 200 OK인지 확인하고, accessToken과 refreshToken이 있는지 확인
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }
}

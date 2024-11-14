package com.bopcon.backend.controller;


import com.bopcon.backend.config.jwt.JwtFactory;
import com.bopcon.backend.config.jwt.JwtProperties;
import com.bopcon.backend.domain.RefreshToken;
import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.CreateAccessTokenRequest;
import com.bopcon.backend.repository.RefreshTokenRepository;
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
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc; // HTTP 요청 및 응답을 테스트하기 위한 클래스
    @Autowired
    protected ObjectMapper objectMapper; // 객체를 JSON 으로 직렬화하거나 역직렬화 하는데 사용
    @Autowired
    private WebApplicationContext context; // Spring 애플리케이션의 컨텍스트 정보를 제공
    @Autowired
    JwtProperties jwtProperties; // jwt 설정 값을 가져오기 위한 빈
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken : 새로운 액세스 토큰을 발급.")
    @Test
    public void createNewAccessToken() throws Exception {
        // given : 테스트 유저 생성, jjwt 라이브러리 이용해 리프레시 토큰을 만들어 db에 저장. 토큰 생성 api 의 요청 본문에 리프레시 토큰을 포함하여 요청 객체 생성
        final String url = "/api/token";
        // 테스트 용 유저
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .nickname("testUser")
                .build());
        // 테스트 유저의 ID 를 포함한 리프레시 토큰 생성, JwtFactory 를 이용해 id 클레임을 포함한 jwt 리프레시 토큰 생성
        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        // 리프레시 토큰을 데이터베이스에 저장
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        // CreateAccessTokenRequest 요청 객체 생성 후, 리프레시 토큰 설정
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request); // 객체를 JSON 문자열로 변환

        // when : 토큰 추가 api 에 요청. 요청 타입 json. given 절에서 만들어둔 객체를 요청 본문으로 함께 보냄.
        ResultActions resultActions = mockMvc.perform(post(url) // MockMvc를 통해 API 요청을 생성. POST 요청
                .contentType(MediaType.APPLICATION_JSON_VALUE) // JSON 으로 설정
                .content(requestBody)); // 요청바디에 본문 담아 보냄.

        // then : 201 인지 확인 후 응답으로 온 액세스 토큰이 비어 있지 않은지 확인.
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}

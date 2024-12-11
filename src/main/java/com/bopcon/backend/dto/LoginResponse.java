package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private Long id;
    private String accessToken;
    private String nickname;
}
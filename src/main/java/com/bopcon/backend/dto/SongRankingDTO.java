package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SongRankingDTO {
    private Long songId;
    private String title;
    private Long count; // 곡 등장 횟수
}
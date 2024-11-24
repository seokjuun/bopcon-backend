package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Long songId;    // 곡 ID
    private String title;   // 곡 제목
    private int count;      // 재생 횟수
    private String ytLink;  // YouTube 링크

    public SongDTO(Long songId, String title, Integer count, String ytLink) {
        this.songId = songId;
        this.title = title;
        this.count = count;
        this.ytLink = ytLink;
    }
}



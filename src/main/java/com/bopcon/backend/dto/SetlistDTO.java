package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetlistDTO {
    private int order;
    private String songName;
    private String ytLink; // YouTube 링크
    private Long songId; // 고유 번호

    // 기존 생성자를 유지하면서 새로운 필드들을 포함하는 생성자 추가
    public SetlistDTO(int order, String songName, Long songId, String ytLink) {
        this.order = order;
        this.songName = songName;
        this.songId = songId;
        this.ytLink = ytLink;
    }


}

package com.bopcon.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteResponse {
    private Long favoriteId; // 즐겨찾기 ID
    private Long artistId;   // 아티스트 ID (nullable)
    private String artistName; // 아티스트 이름 (nullable)
    private String imgUrl;
    private Long newConcertId;  // 콘서트 ID (nullable)
    private String newConcertTitle; // 콘서트 제목 (nullable)
    private String newConcertDate; // 콘서트 날짜 (nullable)
    private String posterUrl;
}

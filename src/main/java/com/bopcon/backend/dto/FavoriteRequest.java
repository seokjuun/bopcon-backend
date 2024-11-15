package com.bopcon.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteRequest {
    private Long artistId; // 아티스트 ID (optional)
    private Long newConcertId; // 콘서트 ID (optional)
}

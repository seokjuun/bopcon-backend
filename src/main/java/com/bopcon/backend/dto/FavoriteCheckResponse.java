package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCheckResponse {
    private boolean isFavorite; // 즐겨찾기 여부
}
package com.bopcon.backend.controller;

import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.FavoriteCheckResponse;
import com.bopcon.backend.dto.FavoriteRequest;
import com.bopcon.backend.dto.FavoriteResponse;
import com.bopcon.backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 아티스트 즐겨찾기 추가
    @PostMapping("/api/favorites/artist/{artistId}")
    public ResponseEntity<String> addArtistFavorite(@PathVariable Long artistId, @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        favoriteService.addArtistFavorite(user, artistId);
        return ResponseEntity.ok("아티스트를 즐겨찾기에 추가했습니다.");
    }

    // 콘서트 즐겨찾기 추가
    @PostMapping("/api/favorites/concert/{concertId}")
    public ResponseEntity<String> addConcertFavorite(@PathVariable Long concertId, @AuthenticationPrincipal User user) {
        favoriteService.addConcertFavorite(user, concertId);
        return ResponseEntity.ok("콘서트를 즐겨찾기에 추가했습니다.");
    }

    // 아티스트 즐겨찾기 삭제
    @DeleteMapping("/api/favorites/artist/{artistId}")
    public ResponseEntity<String> removeArtistFavorite(@PathVariable Long artistId, @AuthenticationPrincipal User user) {
        favoriteService.removeArtistFavorite(user, artistId);
        return ResponseEntity.ok("아티스트를 즐겨찾기에서 삭제했습니다.");
    }

    // 콘서트 즐겨찾기 삭제
    @DeleteMapping("/api/favorites/concert/{concertId}")
    public ResponseEntity<String> removeConcertFavorite(@PathVariable Long concertId, @AuthenticationPrincipal User user) {
        favoriteService.removeConcertFavorite(user, concertId);
        return ResponseEntity.ok("콘서트를 즐겨찾기에서 삭제했습니다.");
    }

    // 유저의 즐겨찾기 목록 조회
    @GetMapping("/api/favorites")
    public ResponseEntity<List<FavoriteResponse>> getUserFavorites(@AuthenticationPrincipal User user) {
        List<FavoriteResponse> favorites = favoriteService.getFavorites(user);
        return ResponseEntity.ok(favorites);
    }

    // 아티스트 즐겨찾기 여부 확인
    @GetMapping("/api/favorites/artist/{artistId}/check")
    public ResponseEntity<FavoriteCheckResponse> checkArtistFavorite(
            @PathVariable Long artistId,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        boolean isFavorite = favoriteService.isArtistFavorite(user, artistId);
        return ResponseEntity.ok(new FavoriteCheckResponse(isFavorite));
    }

    // 콘서트 즐겨찾기 여부 확인
    @GetMapping("/api/favorites/concert/{concertId}/check")
    public ResponseEntity<FavoriteCheckResponse> checkConcertFavorite(
            @PathVariable Long concertId,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        boolean isFavorite = favoriteService.isConcertFavorite(user, concertId);
        return ResponseEntity.ok(new FavoriteCheckResponse(isFavorite));
    }
}

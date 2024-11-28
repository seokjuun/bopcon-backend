package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.Favorite;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.FavoriteRequest;
import com.bopcon.backend.dto.FavoriteResponse;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.FavoriteRepository;
import com.bopcon.backend.repository.NewConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ArtistRepository artistRepository;
    private final NewConcertRepository newConcertRepository;

    @Transactional
    public void addArtistFavorite(User user, Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("아티스트를 찾을 수 없습니다."));
        favoriteRepository.findByUserAndArtist(user, artist)
                .ifPresent(fav -> { throw new IllegalArgumentException("이미 즐겨찾기에 추가된 아티스트입니다."); });

        favoriteRepository.save(Favorite.builder().user(user).artist(artist).build());
    }

    @Transactional
    public void addConcertFavorite(User user, Long concertId) {
        NewConcert newConcert = newConcertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다."));
        favoriteRepository.findByUserAndNewConcert(user, newConcert)
                .ifPresent(fav -> { throw new IllegalArgumentException("이미 즐겨찾기에 추가된 콘서트입니다."); });

        favoriteRepository.save(Favorite.builder().user(user).newConcert(newConcert).build());
    }

    @Transactional
    public void removeArtistFavorite(User user, Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("아티스트를 찾을 수 없습니다."));
        Favorite favorite = favoriteRepository.findByUserAndArtist(user, artist)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기에 없는 아티스트입니다."));

        favoriteRepository.delete(favorite);
    }

    @Transactional
    public void removeConcertFavorite(User user, Long concertId) {
        NewConcert concert = newConcertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다."));
        Favorite favorite = favoriteRepository.findByUserAndNewConcert(user, concert)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기에 없는 콘서트입니다."));

        favoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavorites(User user) {
        List<Favorite> favorites = favoriteRepository.findAllByUser(user);

        return favorites.stream()
                .map(fav -> {
                    FavoriteResponse response = new FavoriteResponse();
                    response.setFavoriteId(fav.getFavoriteId());
                    if (fav.getArtist() != null) {
                        response.setArtistId(fav.getArtist().getArtistId());
                        response.setArtistName(fav.getArtist().getName());
                    }
                    if (fav.getNewConcert() != null) {
                        response.setNewConcertId(fav.getNewConcert().getNewConcertId());
                        response.setNewConcertTitle(fav.getNewConcert().getTitle());
                        response.setNewConcertDate(fav.getNewConcert().getDate().toString());
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }


    // 아티스트 즐겨찾기 여부 확인
    @Transactional(readOnly = true)
    public boolean isArtistFavorite(User user, Long artistId) {
        return favoriteRepository.findByUserAndArtist(user,
                artistRepository.findById(artistId)
                        .orElseThrow(() -> new IllegalArgumentException("아티스트를 찾을 수 없습니다."))
        ).isPresent();
    }

    // 콘서트 즐겨찾기 여부 확인
    @Transactional(readOnly = true)
    public boolean isConcertFavorite(User user, Long concertId) {
        return favoriteRepository.findByUserAndNewConcert(user,
                newConcertRepository.findById(concertId)
                        .orElseThrow(() -> new IllegalArgumentException("콘서트를 찾을 수 없습니다."))
        ).isPresent();
    }
}

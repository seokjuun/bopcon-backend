package com.bopcon.backend.service;

import com.bopcon.backend.api.SetlistApiClient;
import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.SongRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SongService {

    private final SetlistApiClient setlistApiClient;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public SongService(SetlistApiClient setlistApiClient, SongRepository songRepository, ArtistRepository artistRepository) {
        this.setlistApiClient = setlistApiClient;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Transactional
    public Map<String, Integer> fetchAndRankSongs(String mbid) {
        // 1. Artist 조회
        Artist artistId = artistRepository.findByMbid(mbid).orElseThrow(() ->
                new IllegalArgumentException("Invalid MBID provided: Artist not found"));

        Map<String, Integer> songCounts;

        // 2. 데이터베이스 확인
        if (isDatabasePopulated(artistId)) {
            // 데이터베이스에서 곡 정보를 가져옴
            songCounts = getSongsFromDatabase(artistId);
        } else {
            // 외부 API에서 곡 데이터를 가져오고 데이터베이스 업데이트
            songCounts = fetchFromExternalApiAndSave(mbid, artistId);
        }

        // 3. 곡 카운트 정렬
        return songCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }


    // 특정 아티스트의 데이터베이스에 곡이 있는지 확인
    private boolean isDatabasePopulated(Artist artistId) {
        long count = songRepository.countByArtistId(artistId);
        log.info("Song count in database for artist {}: {}", artistId.getName(), count);
        return count > 0;
    }

    // 데이터베이스에서 곡 정보를 가져오는 메서드
    @Transactional(readOnly = true)
    protected Map<String, Integer> getSongsFromDatabase(Artist artistId) {
        log.info("Fetching songs from database for artist: {}", artistId.getName());

        return songRepository.findByArtistId(artistId).stream()
                .collect(Collectors.toMap(
                        Song::getTitle,
                        Song::getCount,
                        Integer::max // 중복 발생 시 더 큰 값을 유지
                ));
    }

    // 외부 API에서 데이터를 가져오고 데이터베이스를 업데이트하는 메서드
    @Transactional
    protected Map<String, Integer> fetchFromExternalApiAndSave(String mbid, Artist artistId) {
        log.info("Fetching songs from external API for MBID: {}", mbid);

        Map<String, Integer> songCounts = new HashMap<>();

        // 외부 API 호출
        JsonNode setlists = setlistApiClient.fetchSetlists(mbid);
        log.debug("Fetched setlists: {}", setlists);

        // JSON 데이터 파싱 및 곡 카운트 생성
        setlists.path("setlist").forEach(setlistNode ->
                setlistNode.path("sets").path("set").forEach(setNode ->
                        setNode.path("song").forEach(songNode -> {
                            String songTitle = songNode.path("name").asText();
                            if (songTitle != null && !songTitle.isEmpty()) {
                                songCounts.merge(songTitle, 1, Integer::sum); // 중복 발생 시 카운트 합산
                            }
                        })
                )
        );

        // 데이터베이스 업데이트
        updateSongDatabase(songCounts, artistId);

        return songCounts;
    }

    @Transactional
    protected void updateSongDatabase(Map<String, Integer> songCounts, Artist artistId) {
        songCounts.forEach((title, count) -> {
            // Title과 artistId로 Song 검색
            Song existingSong = songRepository.findByTitleAndArtistId(title, artistId).orElse(null);
            if (existingSong == null) {
                // 새로운 Song 생성
                Song newSong = Song.create(title, artistId);
                newSong.setCount(count);
                songRepository.save(newSong);
                log.info("Created new song: {} with count: {}", title, count);
            } else {
                // 기존 Song 업데이트
                existingSong.setCount(existingSong.getCount() + count);
                songRepository.save(existingSong);
                log.info("Updated existing song: {} to count: {}", title, existingSong.getCount());
            }
        });
    }
}

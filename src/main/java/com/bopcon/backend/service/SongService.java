package com.bopcon.backend.service;

import com.bopcon.backend.api.SetlistApiClient;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.repository.SongRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SetlistApiClient setlistApiClient;
    private final SongRepository songRepository;

    @Autowired
    public SongService(SetlistApiClient setlistApiClient, SongRepository songRepository) {
        this.setlistApiClient = setlistApiClient;
        this.songRepository = songRepository;
    }

    @Transactional
    public Map<String, Integer> fetchAndRankSongs(String mbid) {
        Map<String, Integer> songCounts;

        // 1. 데이터베이스에 저장된 곡이 이미 있는지 확인
        if (isDatabasePopulated()) {
            // 2. 데이터베이스에서 곡 정보를 가져옴
            songCounts = getSongsFromDatabase();
        } else {
            // 3. 외부 API에서 곡 데이터를 가져오고 데이터베이스를 업데이트
            songCounts = fetchFromExternalApiAndSave(mbid);
        }

        // 곡 카운트를 기준으로 정렬 후 반환
        return songCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // 내림차순 정렬
                .collect(Collectors.toMap(
                        Map.Entry::getKey,       // Key 유지
                        Map.Entry::getValue,     // Value 유지
                        (oldValue, newValue) -> oldValue, // 충돌 시 기존 값 유지
                        LinkedHashMap::new       // 순서 유지
                ));
    }

    // 데이터베이스에 곡이 있는지 확인
    private boolean isDatabasePopulated() {
        return songRepository.count() > 0; // 곡이 하나라도 있는 경우 데이터베이스가 채워져 있다고 간주
    }

    // 데이터베이스에서 곡 정보를 가져오는 메서드
    @Transactional(readOnly = true)
    protected Map<String, Integer> getSongsFromDatabase() {
        return songRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Song::getTitle,
                        Song::getCount
                ));
    }

    // 외부 API에서 데이터를 가져오고 데이터베이스를 업데이트하는 메서드
    @Transactional
    protected Map<String, Integer> fetchFromExternalApiAndSave(String mbid) {
        Map<String, Integer> songCounts = new HashMap<>();

        // 외부 API에서 데이터를 가져옴
        JsonNode setlists = setlistApiClient.fetchSetlists(mbid);

        // 가져온 데이터에서 필요한 정보를 파싱하고 카운트
        setlists.path("setlist").forEach(setlistNode ->
                setlistNode.path("sets").path("set").forEach(setNode ->
                        setNode.path("song").forEach(songNode -> {
                            String songTitle = songNode.path("name").asText();
                            if (songTitle != null && !songTitle.isEmpty()) {
                                songCounts.put(songTitle, songCounts.getOrDefault(songTitle, 0) + 1);
                            }
                        })
                )
        );

        // 데이터베이스 업데이트
        updateSongDatabase(songCounts);

        return songCounts;
    }

    @Transactional
    protected void updateSongDatabase(Map<String, Integer> songCounts) {
        songCounts.forEach((title, count) -> {
            Song song = songRepository.findByTitle(title).orElse(null);
            if (song == null) {
                Song newSong = Song.create(title);
                newSong.setCount(count); // 초기 값 설정
                songRepository.save(newSong);
                System.out.println("Created new song: " + title + " with count: " + count);
            } else {
                System.out.println("Updating existing song: " + title + " from " + song.getCount() + " to " + count);
                song.setCount(count);
                songRepository.save(song);
            }
        });
    }




}


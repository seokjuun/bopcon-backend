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

    @Transactional
    protected void updateSongDatabase(Map<String, Integer> songCounts) {
        songCounts.forEach((title, count) -> {
            // 곡이 데이터베이스에 있는지 확인
            Song song = songRepository.findByTitle(title).orElseGet(() -> {
                // 새로운 곡이라면 저장
                Song newSong = Song.create(title); // 정적 팩토리 메서드 사용
                return songRepository.save(newSong);
            });

            // 곡의 count를 업데이트
            song.setCount(song.getCount() + count); // 기존 값에 추가
            songRepository.save(song);
        });
    }
}

package com.bopcon.backend.service;

import com.bopcon.backend.api.SetlistApiClient;
import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.ConcertSetlist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.dto.*;
import com.bopcon.backend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final PastConcertRepository pastConcertRepository;
    private final SongRepository songRepository;
    private final ConcertSetlistRepository concertSetlistRepository;
    private final SetlistApiClient setlistApiClient; // 외부 API 클라이언트

    // 아티스트 추가 메서드
    @CacheEvict(value = {"allArtists", "singleArtist"}, allEntries = true)
    public Artist save(AddArtistRequest request) {  return artistRepository.save(request.toArtist());}

    // 아티스트 목록 가져오기
    @Cacheable(value = "allArtists", key = "'allArtists'")
    public List<Artist> findAllArtists(){ return artistRepository.findAll(); }

    // 아티스트 조회
    @Cacheable(value = "singleArtist", key = "#artistId")
    public Artist findByArtistId(long artistId){
        return artistRepository.findById(artistId)
                .orElseThrow(()-> new IllegalArgumentException("not found" + artistId));
    }

    // 아시트스 수정
    @Transactional
    @CacheEvict(value = {"allArtists", "singleArtist"}, allEntries = true)
    public Artist update(long artistId, UpdateArtistRequest request) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()-> new IllegalArgumentException("not found" + artistId));

        artist.updateArtist(request);
        return artist;
    }

    // 아티스트 삭제
    @CacheEvict(value = {"allArtists", "singleArtist"}, allEntries = true)
    public void delete(long artistId){
        artistRepository.deleteById(artistId);
    }

    // 1. 외부 API 데이터를 가져와 아티스트와 관련된 데이터를 저장
    @Transactional
    public void syncArtistData(String mbid) {
        Artist artist = artistRepository.findByMbid(mbid)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with MBID: " + mbid));

        // 1.1 외부 API에서 콘서트 데이터 가져오기
        JsonNode setlistsJson = setlistApiClient.fetchSetlists(mbid);

        // 1.2 콘서트 및 셋리스트 저장
        setlistsJson.get("setlist").forEach(setlistNode -> {
            savePastConcertAndSetlist(artist, setlistNode);
        });
    }

    // 1.2 콘서트와 셋리스트 저장
    private void savePastConcertAndSetlist(Artist artist, JsonNode setlistNode) {
        // 1.2.1 PastConcert 저장
        PastConcert pastConcert = new PastConcert(
                artist,
                setlistNode.get("venue").get("name").asText(),
                setlistNode.get("venue").get("city").get("name").asText(),
                setlistNode.get("venue").get("city").get("country").get("name").asText(),
                LocalDate.parse(setlistNode.get("eventDate").asText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );
        pastConcertRepository.save(pastConcert);

        // 1.2.2 셋리스트 저장
        setlistNode.get("sets").get("set").forEach(setNode -> {
            saveSetlist(pastConcert, artist, setNode);
        });
    }

    // 1.2.3 셋리스트 저장
    private void saveSetlist(PastConcert pastConcert, Artist artist, JsonNode setNode) {
        AtomicInteger orderCounter = new AtomicInteger(1); // 순서를 계산하기 위한 Counter
        setNode.get("song").forEach(songNode -> {
            String songTitle = songNode.get("name").asText();

            // Song 존재 여부 확인 및 저장
            Song song = songRepository.findByTitleAndArtist_ArtistId(songTitle, artist.getArtistId())
                    .orElseGet(() -> {
                        Song newSong = new Song(artist, songTitle, null, null);
                        return songRepository.save(newSong);
                    });
            // order 값 설정
            int order = orderCounter.getAndIncrement();

            // ConcertSetlist 저장
            ConcertSetlist concertSetlist = new ConcertSetlist(pastConcert, song, order);
            concertSetlistRepository.save(concertSetlist);
        });
    }

    // 2. 특정 아티스트의 과거 공연 데이터 반환
    @Transactional
    public List<PastConcertDTO> getArtistPastConcerts(Long artistId) {
        return pastConcertRepository.findAllByArtist_ArtistId(artistId).stream()
                .map(PastConcertDTO::fromEntity) // DTO로 변환
                .collect(Collectors.toList());
    }

    // 곡 랭킹 조회
    @Transactional
    public List<SongRankingDTO> getSongRankingByArtist(Long artistId) {
        return concertSetlistRepository.findSongRankingByArtistId(artistId);
    }

    // 특정 아티스트의 과거 콘서트 셋리스트 반환
    @Transactional
    public List<PastConcertSetlistDTO> getPastConcertSetlistsByArtist(Long artistId) {
        return pastConcertRepository.findConcertSetlistsByArtistId(artistId);
    }
}

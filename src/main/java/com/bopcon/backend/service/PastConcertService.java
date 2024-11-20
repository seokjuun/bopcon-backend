package com.bopcon.backend.service;

import com.bopcon.backend.api.SetlistApiClient;
import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.ConcertSetlist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.dto.PastConcertResponse;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.ConcertSetlistRepository;
import com.bopcon.backend.repository.PastConcertRepository;
import com.bopcon.backend.repository.SongRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PastConcertService {

    private final SetlistApiClient setlistApiClient;
    private final ArtistRepository artistRepository;
    private final PastConcertRepository pastConcertRepository;
    private final ConcertSetlistRepository concertSetlistRepository;
    private final SongRepository songRepository;

    private static final Logger logger = LoggerFactory.getLogger(PastConcertService.class);

    public void fetchAndSavePastConcerts(String mbid) {
        try {
            JsonNode root = setlistApiClient.fetchSetlists(mbid);

            for (JsonNode setlistNode : root.path("setlist")) {
                PastConcert pastConcert = savePastConcert(setlistNode, mbid);

                if (pastConcert != null) {
                    saveConcertSetlist(setlistNode, pastConcert);
                }
            }
        } catch (Exception e) {
            logger.error("Error while fetching and saving past concerts for mbid: {}", mbid, e);
            throw new RuntimeException("Failed to fetch and save past concerts for mbid: " + mbid, e);
        }
    }

    private PastConcert savePastConcert(JsonNode setlistNode, String mbid) {
        String venueName = getNodeValue(setlistNode, "venue.name");
        String cityName = getNodeValue(setlistNode, "venue.city.name");
        String country = getNodeValue(setlistNode, "venue.city.country");
        String date = getNodeValue(setlistNode, "eventDate");

        if (venueName == null || cityName == null || date == null) {
            logger.warn("Invalid data: venueName={}, cityName={}, date={}", venueName, cityName, date);
            return null;
        }

        Artist artist = artistRepository.findByMbid(mbid)
                .orElseThrow(() -> new RuntimeException("Artist not found with mbid: " + mbid));

        LocalDateTime eventDate;
        try {
            eventDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format: " + date, e);
        }

        return pastConcertRepository.findByDateAndVenueNameAndCityName(eventDate, venueName, cityName)
                .orElseGet(() -> {
                    PastConcert pastConcert = PastConcert.builder()
                            .artistId(artist)
                            .venueName(venueName)
                            .cityName(cityName)
                            .country(country)
                            .date(eventDate)
                            .build();
                    logger.info("Saving new PastConcert: {}", pastConcert);
                    return pastConcertRepository.save(pastConcert);
                });
    }

    private void saveConcertSetlist(JsonNode setlistNode, PastConcert pastConcert) {
        JsonNode setsNode = setlistNode.path("sets").path("set");

        if (setsNode.isArray() && setsNode.size() > 0) {
            int order = 1;

            // 해당 PastConcert에 이미 저장된 곡 제목 가져오기 (한 번만 호출)
            List<String> existingSongTitles = concertSetlistRepository.findSongTitlesByPastConcertId(pastConcert.getPastConcertId());

            // JSON의 모든 set 순회
            for (JsonNode setNode : setsNode) {
                for (JsonNode songNode : setNode.path("song")) {
                    String songTitle = songNode.path("name").asText(null);

                    if (isValidSong(songTitle) && !existingSongTitles.contains(songTitle)) {
                        // 곡 정보를 데이터베이스에서 조회하거나 새로 생성
                        Song song = songRepository.findByTitleAndArtistId(songTitle, pastConcert.getArtistId())
                                .orElseGet(() -> songRepository.save(Song.builder()
                                        .artistId(pastConcert.getArtistId())
                                        .title(songTitle)
                                        .count(1) // 초기 재생 횟수
                                        .ytLink(null)
                                        .build()));

                        // 새로운 ConcertSetlist 엔티티 생성 및 저장
                        ConcertSetlist concertSetlist = ConcertSetlist.builder()
                                .pastConcert(pastConcert)
                                .song(song)
                                .order(order++) // 곡 순서 설정
                                .build();

                        concertSetlistRepository.save(concertSetlist);
                        logger.info("Added song '{}' to concert setlist for '{}'", songTitle, pastConcert.getVenueName());

                        // 추가된 곡 제목을 목록에 추가
                        existingSongTitles.add(songTitle);
                    } else {
                        logger.info("Duplicate entry skipped for song: {}", songTitle);
                    }
                }
            }
        }
    }

    private boolean isValidSong(String songTitle) {
        return songTitle != null && !songTitle.equalsIgnoreCase("Ment") && !songTitle.toLowerCase().contains("ment");
    }

    private String getNodeValue(JsonNode node, String path) {
        String[] keys = path.split("\\.");
        for (String key : keys) {
            if (node == null) return null;
            node = node.path(key);
        }
        return node.asText(null);
    }

    public List<PastConcertResponse> getAllPastConcerts() {
        return pastConcertRepository.findAll()
                .stream()
                .map(PastConcertResponse::new)
                .collect(Collectors.toList());
    }

    public List<PastConcert> getPastConcertsByArtist(String mbid) {
        return pastConcertRepository.findByArtistId_Mbid(mbid);
    }

    public PastConcert getPastConcertById(Long concertId) {
        return pastConcertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("Past concert not found with ID: " + concertId));
    }

    public List<PastConcert> getPastConcertsByArtistName(String name) {
        List<PastConcert> concerts = pastConcertRepository.findByArtistId_Name(name);
        if (concerts == null || concerts.isEmpty()) {
            throw new RuntimeException("No past concerts found for artist: " + name);
        }
        return concerts;
    }
}

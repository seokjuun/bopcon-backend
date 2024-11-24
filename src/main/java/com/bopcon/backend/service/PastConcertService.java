package com.bopcon.backend.service;

import com.bopcon.backend.api.SetlistApiClient;
import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.ConcertSetlist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.PastConcertResponse;
import com.bopcon.backend.dto.SetlistDTO;
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
            // 셋리스트 API 호출
            JsonNode root = setlistApiClient.fetchSetlists(mbid);

            // API에서 반환된 콘서트 리스트 가져오기
            JsonNode setlists = root.path("setlist");
            if (!setlists.isArray()) {
                logger.warn("No setlists found for mbid: {}", mbid);
                return;
            }

            // 최대 20개의 콘서트만 처리
            int concertLimit = 20;
            int processedCount = 0;

            for (JsonNode setlistNode : setlists) {
                if (processedCount >= concertLimit) {
                    break; // 20개를 초과하면 중지
                }

                PastConcert pastConcert = savePastConcert(setlistNode, mbid);
                if (pastConcert != null) {
                    saveConcertSetlist(setlistNode, pastConcert);
                    processedCount++;
                }
            }

            logger.info("Processed {} concerts for mbid: {}", processedCount, mbid);

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

            // 기존의 Song ID를 미리 조회하여 Map으로 저장
            Map<String, Song> existingSongs = songRepository
                    .findAllByArtistId(pastConcert.getArtistId())
                    .stream()
                    .collect(Collectors.toMap(Song::getTitle, song -> song));

            // 기존의 ConcertSetlist 데이터 미리 조회
            Set<Long> existingSetlists = concertSetlistRepository
                    .findAllByPastConcert(pastConcert)
                    .stream()
                    .map(setlist -> setlist.getSongId().getSongId())
                    .collect(Collectors.toSet());

            // JSON의 모든 set 순회
            for (JsonNode setNode : setsNode) {
                for (JsonNode songNode : setNode.path("song")) {
                    String songTitle = songNode.path("name").asText(null);

                    if (isValidSong(songTitle)) {
                        // 곡 정보가 기존 데이터에 없는 경우 새로 추가
                        Song song = existingSongs.getOrDefault(songTitle, null);
                        if (song == null) {
                            song = songRepository.save(Song.builder()
                                    .artistId(pastConcert.getArtistId())
                                    .title(songTitle)
                                    .count(1) // 초기 카운트 1로 설정
                                    .ytLink(null)
                                    .build());
                            existingSongs.put(songTitle, song);
                        } else {
                            // 곡 카운트 업데이트
                            song.setCount(song.getCount() + 1);
                            songRepository.save(song);
                        }

                        // ConcertSetlist에 추가되지 않은 경우만 저장
                        if (!existingSetlists.contains(song.getSongId())) {
                            ConcertSetlist concertSetlist = ConcertSetlist.builder()
                                    .pastConcert(pastConcert)
                                    .song(song)
                                    .order(order++) // 곡 순서 설정
                                    .build();
                            concertSetlistRepository.save(concertSetlist);
                            existingSetlists.add(song.getSongId());
                        }
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

//    public List<PastConcert> getPastConcertsByArtist(String mbid) {
//        return pastConcertRepository.findByArtistId_Mbid(mbid);
//    }

    public PastConcert getPastConcertById(Long concertId) {
        return pastConcertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("Past concert not found with ID: " + concertId));
    }

    @Transactional
    public List<PastConcertDTO> getPastConcertsByArtistName(String artistName) {
        List<PastConcert> pastConcerts = pastConcertRepository.findByArtistId_Name(artistName);

        if (pastConcerts.isEmpty()) {
            throw new IllegalArgumentException("No past concerts found for artist: " + artistName);
        }

        return pastConcerts.stream().map(this::convertToDTO).toList();
    }

    private PastConcertDTO convertToDTO(PastConcert pastConcert) {
        PastConcertDTO dto = new PastConcertDTO();
        dto.setPastConcertId(pastConcert.getPastConcertId());
        dto.setVenueName(pastConcert.getVenueName());
        dto.setCityName(pastConcert.getCityName());
        dto.setDate(pastConcert.getDate().toLocalDate().toString());

//        List<SetlistDTO> setlists = pastConcert.getSetlists().stream()
//                .map(setlist -> new SetlistDTO(setlist.getOrder(), setlist.getSongId().getTitle()))
//                .toList();
//
//        dto.setSetlists(setlists);
        return dto;
    }

}



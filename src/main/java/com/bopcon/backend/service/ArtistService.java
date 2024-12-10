package com.bopcon.backend.service;

import com.bopcon.backend.api.GeminiApiClient;
import com.bopcon.backend.api.SetlistApiClient;
import com.bopcon.backend.domain.*;
import com.bopcon.backend.dto.*;
import com.bopcon.backend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final GeminiApiClient geminiApiClient;
    private final PredictSetlistRepository predictSetlistRepository;
    private final NewConcertRepository newConcertRepository;
    private final ObjectMapper objectMapper;
    private final S3Service s3Service;


    // 아티스트 추가 메서드
    @CacheEvict(value = {"allArtists", "singleArtist"}, allEntries = true)
    public Artist save(AddArtistRequest request, MultipartFile file) {
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            // S3 업로드 로직 호출 (예: s3Service.upload(file))
            imageUrl = s3Service.upload(file, "artist-images");
            // 업로드된 s3 URL을 request에 반영
            request = new AddArtistRequest(
                    request.getMbid(),
                    request.getName(),
                    request.getKrName(),
                    imageUrl, // imgUrl 갱신
                    request.getSnsUrl(),
                    request.getMediaUrl()
            );
        }
        return artistRepository.save(request.toArtist());
    }

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
    public Artist update(long artistId, UpdateArtistRequest request, MultipartFile file) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("not found" + artistId));

        String imageUrl = artist.getImgUrl(); // 기존 이미지 URL
        if (file != null && !file.isEmpty()) {
            imageUrl = s3Service.upload(file, "artist-images");
        }

        // 갱신된 imageUrl로 request를 다시 구성
        UpdateArtistRequest updatedRequest = new UpdateArtistRequest(
                request.getMbid(),
                request.getName(),
                request.getKrName(),
                imageUrl,
                request.getSnsUrl(),
                request.getMediaUrl()
        );

        artist.updateArtist(updatedRequest);
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

//    @Transactional
//    public void processPredictedSetlist(Long artistId, Long newConcertId, String pastSetlistJson) {
//        // 1. 프롬프트 생성
//        String prompt = createPrompt(pastSetlistJson);
//
//        // 2. Gemini API 호출
//        JsonNode predictedSetlistJson = geminiApiClient.generatePredictedSetlist(prompt);
//
//        // 3. NewConcert 가져오기
//        NewConcert newConcert = newConcertRepository.findById(newConcertId)
//                .orElseThrow(() -> new IllegalArgumentException("New Concert not found"));
//        // 4. Artist 가져오기
//        Artist artist = artistRepository.findById(artistId)
//                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));
//        // 4. 예상 셋리스트 저장
//        AtomicInteger orderCounter = new AtomicInteger(1);
//        predictedSetlistJson.forEach(songNode -> {
//            String songTitle = songNode.get("songTitle").asText();
//
//            // Song 매칭 및 저장
//            Song song = songRepository.findByTitleAndArtist_ArtistId(songTitle, artistId)
//                    .orElseGet(() -> {
//                        Song newSong = Song.builder()
//                                .artist(artist)
//                                .title(songTitle)
//                                .lyrics(null) // 초기값 null
//                                .ytLink(null) // 초기값 null
//                                .build();
//                        return songRepository.save(newSong);
//                    });
//
//            // PredictSetlist 저장
//            PredictSetlist predictSetlist = PredictSetlist.builder()
//                    .newConcert(newConcert)
//                    .song(song)
//                    .order(orderCounter.getAndIncrement())
//                    .build();
//            predictSetlistRepository.save(predictSetlist);
//        });
//    }

    @Transactional
    public void processPredictedSetlist(Long artistId, Long newConcertId, String pastSetlistJson) {
        // 1. 프롬프트 생성
        String prompt = createPrompt(pastSetlistJson);

        // 2. Gemini API 호출
        JsonNode geminiResponse = geminiApiClient.generatePredictedSetlist(prompt);

        // 3. text 필드에서 JSON 추출
        String predictedSetlistText = geminiResponse.get("candidates")
                .get(0)
                .get("content")
                .get("parts")
                .get(0)
                .get("text")
                .asText();

        // 4. 불필요한 ```json 및 ``` 제거
        String cleanJson = predictedSetlistText
                .replace("```json", "")
                .replace("```", "")
                .trim();

        // 5. JSON 파싱
        JsonNode predictedSetlistJson;
        try {
            predictedSetlistJson = objectMapper.readTree(cleanJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse predicted setlist JSON", e);
        }

        // 6. NewConcert 가져오기
        NewConcert newConcert = newConcertRepository.findById(newConcertId)
                .orElseThrow(() -> new IllegalArgumentException("New Concert not found"));

        // 7. 예상 셋리스트 저장
        AtomicInteger orderCounter = new AtomicInteger(1);
        predictedSetlistJson.forEach(songNode -> {
            String songTitle = songNode.get("songTitle").asText();

            // Song 매칭 및 저장
            Song song = songRepository.findByTitleAndArtist_ArtistId(songTitle, artistId)
                    .orElseGet(() -> {
                        Song newSong = Song.builder()
                                .artist(newConcert.getArtist()) // Artist 설정
                                .title(songTitle)
                                .build();
                        return songRepository.save(newSong);
                    });

            // ConcertSetlist 저장
            PredictSetlist predictSetlist = PredictSetlist.builder()
                    .newConcert(newConcert)
                    .song(song)
                    .order(orderCounter.getAndIncrement())
                    .build();
            predictSetlistRepository.save(predictSetlist);
        });
    }

    private String createPrompt(String pastSetlistJson) {
        return """
            당신은 "콘서트 분석 전문가 AI"입니다. 아티스트의 최근 공연 데이터를 분석하고, 트렌드, 곡 순서, 반복 빈도를 기반으로 다음 공연의 예상 셋리스트를 생성하는 것이 목표입니다.
            아래는 아티스트의 최근 20개 공연 데이터를 JSON 형식으로 제공한 예시입니다. 이 데이터를 분석하여 다음과 같은 가정을 기반으로 예상 셋리스트를 생성하세요:
            1. 자주 반복되는 곡은 다음 공연에도 포함될 가능성이 높습니다.
            2. 곡의 순서는 최근 공연의 패턴을 따르되, 마지막 공연의 변화를 반영합니다.
            3. 공연 시간은  1시간에서 2시간 내외로, 15곡에서 ~ 25곡 사이에서 조절합니다.
            아래는 최근 공연 데이터입니다:
            %s
            위 데이터를 기반으로 다음 공연의 예상 셋리스트를 생성하세요. 출력 형식은  셋리스트에 대한 JSON만 제공합니다.
            아웃풋 형식:
            - JSON 형식의 예상 셋리스트
            -
                 [
                    {
                      "songTitle": "example01",
                      "order": 1
                    },
                    {
                      "songTitle": "example02",
                      "order": 2
                    },
                    {
                      "songTitle": "example03",
                      "order": 3
                    }, ...
                 ]
            """.formatted(pastSetlistJson.replace("\"", "\\\""));
    }

    @Transactional
    public List<PredictSetlistDTO> getPredictedSetlist(Long newConcertId) {
        return predictSetlistRepository.findAllByNewConcert_NewConcertId(newConcertId).stream()
                .map(ps -> new PredictSetlistDTO(
                        ps.getSong().getTitle(),
                        ps.getOrder(),
                        ps.getSong().getLyrics(),
                        ps.getSong().getYtLink()))
                .collect(Collectors.toList());
    }
}

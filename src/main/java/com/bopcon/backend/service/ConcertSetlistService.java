package com.bopcon.backend.service;

import com.bopcon.backend.domain.*;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.SetlistDTO;
import com.bopcon.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import me.kimminhyuk.SolarClient; // SolarClient 클래스


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class    ConcertSetlistService {

    private final PastConcertRepository pastConcertRepository;
    private final SongRepository songRepository;
    private final ConcertSetlistRepository concertSetlistRepository;
    private final ArtistRepository artistRepository;
    private final NewConcertRepository newConcertRepository;
    private final SolarClient solarClient;

    @Autowired
    public ConcertSetlistService(PastConcertRepository pastConcertRepository,
                                 SongRepository songRepository,
                                 ConcertSetlistRepository concertSetlistRepository,
                                 ArtistRepository artistRepository,
                                 NewConcertRepository newConcertRepository) {
        this.pastConcertRepository = pastConcertRepository;
        this.songRepository = songRepository;
        this.concertSetlistRepository = concertSetlistRepository;
        this.artistRepository = artistRepository;
        this.newConcertRepository = newConcertRepository;
        this.solarClient = new SolarClient();
    }

    private static final Logger log = LoggerFactory.getLogger(PastConcertService.class);


    /**
     * Solar API를 사용하여 특정 아티스트의 예상 셋리스트 생성
     *
     * @param artistId 아티스트 ID
     * @return 예상 셋리스트
     */
    public Mono<String> generatePredictedSetlist(Long artistId) {
        // 1. 아티스트 정보 조회
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("Artist with ID " + artistId + " not found."));

        String artistName = artist.getName();

        // 2. 과거 콘서트 데이터 수집
        List<PastConcert> pastConcerts = pastConcertRepository.findByArtistId_ArtistId(artistId);

        if (pastConcerts.isEmpty()) {
            return Mono.error(new IllegalArgumentException("No past concerts found for the artist."));
        }

        // 3. 과거 콘서트에서 곡 제목 수집
        List<String> songTitles = pastConcerts.stream()
                .flatMap(pastConcert -> concertSetlistRepository
                        .findByPastConcert_PastConcertIdOrderByOrder(pastConcert.getPastConcertId())
                        .stream()
                        .map(setlist -> setlist.getSongId().getTitle()))
                .collect(Collectors.toList());

        if (songTitles.isEmpty()) {
            return Mono.error(new IllegalArgumentException("No songs found in the past concerts."));
        }

        // 4. 곡 제목 데이터를 Solar API에 전달할 형식으로 변환
        String songData = String.join(", ", songTitles);

        // 5. Solar API 호출 프롬프트 및 메시지 구성
        String prompt = "당신은 과거 콘서트 데이터를 기반으로 다음 콘서트 셋리스트를 예측하는 AI입니다.";
        String message = String.format("아티스트 [%s]의 과거 콘서트 곡 목록: [%s]. " +
                "이 데이터를 기반으로 예상 셋리스트를 생성해 주세요. " +
                "각 곡의 순서를 포함해야 합니다.", artistName, songData);

        // 6. Solar API 호출 및 결과 반환
        return solarClient.sendMessage(prompt, message)
                .doOnNext(result -> System.out.println("예상 셋리스트 생성 성공: " + result))
                .doOnError(error -> System.err.println("예상 셋리스트 생성 중 오류 발생: " + error.getMessage()));
    }

    /**
     * 특정 NewConcert의 Setlist 삭제
     *
     * @param newConcert NewConcert ID
     */
    @Transactional
    public void deleteSetlistForConcert(Long newConcert) {
        concertSetlistRepository.deleteByNewConcertId(newConcert);
    }

    /**
     * 특정 NewConcert의 Setlist 저장
     *
     * @param newConcert NewConcert 객체
     * @param setlistJson 예상 셋리스트 데이터
     */
    @Transactional
    public void savePredictedSetlistToDatabase(NewConcert newConcert, List<Map<String, Object>> setlistJson) {
        // 1. 기존 Setlist 삭제
        deleteSetlistForConcert(newConcert.getNewConcertId());

        // 2. 셋리스트에서 곡 제목 추출
        List<String> songTitles = setlistJson.stream()
                .map(songData -> (String) songData.get("title"))
                .distinct() // 중복 제거
                .collect(Collectors.toList());

        // 3. 데이터베이스에서 기존 곡들 조회
        Map<String, Song> existingSongs = songRepository
                .findAllByArtistIdAndTitleIn(newConcert.getArtistId().getArtistId(), songTitles)
                .stream()
                .collect(Collectors.toMap(
                        Song::getTitle,
                        song -> song,
                        (song1, song2) -> song1 // 중복 키 발생 시 첫 번째 곡 유지
                ));

        // 4. 새로운 곡 추가
        List<Song> newSongs = songTitles.stream()
                .filter(title -> !existingSongs.containsKey(title)) // 기존에 없는 곡만 필터링
                .map(title -> Song.builder()
                        .artistId(newConcert.getArtistId())
                        .title(title)
                        .count(0) // 초기 count 값
                        .ytLink(null)
                        .build())
                .toList();

        // 5. 새 곡을 DB에 저장
        List<Song> savedNewSongs = songRepository.saveAll(newSongs);

        // 6. 모든 곡을 합친 Map 생성
        Map<String, Song> allSongs = new HashMap<>(existingSongs);
        savedNewSongs.forEach(song -> allSongs.put(song.getTitle(), song));

        // 7. ConcertSetlist 생성 및 저장
        List<ConcertSetlist> setlists = setlistJson.stream()
                .filter(songData -> allSongs.containsKey(songData.get("title"))) // 유효한 곡만 필터링
                .map(songData -> {
                    String title = (String) songData.get("title");
                    Song song = allSongs.get(title); // Map에서 Song 가져오기
                    int order = (int) songData.getOrDefault("order", 0);

                    return ConcertSetlist.builder()
                            .newConcert(newConcert)
                            .song(song)
                            .order(order)
                            .build();
                })
                .toList();

        // 저장
        concertSetlistRepository.saveAll(setlists);
    }

    /**
     * NewConcert의 상태에 따른 Setlist 처리
     *
     * @param newConcertId NewConcert ID
     * @param concertStatus       Concert 상태 (UPCOMING 또는 COMPLETE)
     */
    @Transactional
    public void handleConcertSetlistStatus(Long newConcertId, NewConcert.ConcertStatus concertStatus) {
        if (concertStatus == null) {
            throw new IllegalArgumentException("Concert status cannot be null");
        }

        NewConcert concert = newConcertRepository.findById(newConcertId)
                .orElseThrow(() -> new IllegalArgumentException("NewConcert with ID " + newConcertId + " not found."));

        // 상태를 변경하기 전에 로그 추가
        log.info("Updating concert status for ID {}: {}", newConcertId, concertStatus);

        if (concertStatus == NewConcert.ConcertStatus.COMPLETED) {
            deleteSetlistForConcert(newConcertId);
        }

        concert.setConcertStatus(concertStatus);
        newConcertRepository.save(concert);
    }




    /**
     * 특정 아티스트의 과거 콘서트 셋리스트를 가져옵니다.
     *
     * @param artistId 아티스트 ID
     * @return PastConcertDTO 리스트
     */
    @Transactional(readOnly = true)
    public List<PastConcertDTO> getSetlistsByArtistId(Long artistId) {
        // 1. 특정 아티스트의 과거 콘서트를 조회합니다.
        List<PastConcert> pastConcerts = pastConcertRepository.findByArtistId_ArtistId(artistId);

        if (pastConcerts.isEmpty()) {
            throw new IllegalArgumentException("No concerts found for the given artist ID.");
        }

        // 2. PastConcert 엔티티를 DTO로 변환합니다.
        return pastConcerts.stream().map(concert -> {
            PastConcertDTO concertDTO = new PastConcertDTO();
            concertDTO.setPastConcertId(concert.getPastConcertId());
            concertDTO.setVenueName(concert.getVenueName());
            concertDTO.setCityName(concert.getCityName());
            concertDTO.setDate(concert.getDate().toString());

            // 셋리스트를 DTO로 변환
            List<SetlistDTO> setlists = concert.getSetlists().stream()
                    .map(setlist -> new SetlistDTO(
                            setlist.getOrder(), // 순서
                            setlist.getSongId().getTitle(), // 곡 제목
                            setlist.getSongId().getSongId(), // 고유 번호
                            setlist.getSongId().getYtLink() // YouTube 링크
                    ))
                    .toList();


            concertDTO.setSetlists(setlists);
            return concertDTO;
        }).toList();
    }

    @Transactional(readOnly = true)
    public PastConcert findPastConcertById(Long pastConcertId) {
        return pastConcertRepository.findById(pastConcertId)
                .orElseThrow(() -> new IllegalArgumentException("PastConcert with ID " + pastConcertId + " not found."));
    }

    @Transactional(readOnly = true)
    public List<SetlistDTO> getSetlistByPastConcertId(Long pastConcert) {
        return concertSetlistRepository.findByPastConcert_PastConcertIdOrderByOrder(pastConcert)
                .stream()
                .map(setlist -> new SetlistDTO(
                        setlist.getOrder(),
                        setlist.getSongId().getTitle(), // 곡 제목
                        setlist.getSongId().getSongId(), // 고유 번호
                        setlist.getSongId().getYtLink() // YouTube 링크
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SetlistDTO> getSetlistByNewConcertId(Long newConcertId) {
        return concertSetlistRepository.findByNewConcert_NewConcertIdOrderByOrder(newConcertId)
                .stream()
                .map(setlist -> new SetlistDTO(
                        setlist.getOrder(),
                        setlist.getSongId().getTitle(), // 곡 제목
                        setlist.getSongId().getSongId(), // 고유 번호
                        setlist.getSongId().getYtLink() // YouTube 링크
                ))
                .toList();
    }


}

package com.bopcon.backend.service;

import com.bopcon.backend.domain.*;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.SetlistDTO;
import com.bopcon.backend.repository.ArtistRepository;
import com.bopcon.backend.repository.PastConcertRepository;
import com.bopcon.backend.repository.SongRepository;
import com.bopcon.backend.repository.ConcertSetlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import me.kimminhyuk.SolarClient; // SolarClient 클래스


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConcertSetlistService {

    private final PastConcertRepository pastConcertRepository;
    private final SongRepository songRepository;
    private final ConcertSetlistRepository concertSetlistRepository;
    private final ArtistRepository artistRepository;
    private final SolarClient solarClient; // SolarClient 추가



    @Autowired
    public ConcertSetlistService(PastConcertRepository pastConcertRepository,
                                 SongRepository songRepository,
                                 ConcertSetlistRepository concertSetlistRepository,
                                 ArtistRepository artistRepository
                                 ) {
        this.pastConcertRepository = pastConcertRepository;
        this.songRepository = songRepository;
        this.concertSetlistRepository = concertSetlistRepository;
        this.artistRepository = artistRepository;
        this.solarClient = new SolarClient();

    }

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
     * 특정 PastConcert의 셋리스트를 가져옵니다.
     *
     * @param pastConcert 과거 콘서트 ID
     * @return 셋리스트에 포함된 곡 제목 목록
     */
    @Transactional
    public List<SetlistDTO> getSetlistByPastConcertId(PastConcert pastConcert) {
        return concertSetlistRepository.findByPastConcert_PastConcertIdOrderByOrder(pastConcert.getPastConcertId())
                .stream()
                .map(setlist -> new SetlistDTO(
                        setlist.getOrder(),
                        setlist.getSongId().getTitle()
                ))
                .toList();
    }



    /**
     * 특정 아티스트의 mbid를 통해 과거 콘서트를 가져옵니다.
     *
     * @param mbid 아티스트 mbid
     * @return 해당 아티스트의 과거 콘서트 목록
     */
    public List<PastConcert> getPastConcertsByArtistMbid(String mbid) {
        return pastConcertRepository.findByArtistId_Mbid(mbid);
    }

    /**
     * 새로운 곡을 추가하거나 기존 곡의 재생 횟수를 증가시킵니다.
     *
     * @param artistId 아티스트 ID
     * @param songTitle 곡 제목
     * @return 저장된 Song 엔티티
     */
    @Transactional
    public Song saveOrUpdateSong(Artist artistId, String songTitle) {
        Optional<Song> existingSong = songRepository.findByArtistIdAndTitle(artistId, songTitle);

        if (existingSong.isPresent()) {
            Song song = existingSong.get();
            song.incrementCount();
            return songRepository.save(song);
        } else {
            Song newSong = Song.builder()
                    .artistId(artistId)
                    .title(songTitle)
                    .count(1)
                    .build();
            return songRepository.save(newSong);
        }
    }

    /**
     * 특정 PastConcert에 곡들을 추가합니다.
     *
     * @param pastConcertId 과거 콘서트 ID
     * @param songTitles 곡 제목 리스트
     */
    @Transactional
    public void addSongsToPastConcert(Long pastConcertId, List<String> songTitles) {
        // 1. 과거 콘서트 가져오기
        Optional<PastConcert> optionalPastConcert = pastConcertRepository.findById(pastConcertId);

        if (optionalPastConcert.isEmpty()) {
            throw new IllegalArgumentException("PastConcert with ID " + pastConcertId + " not found.");
        }

        PastConcert pastConcert = optionalPastConcert.get();

        // 2. 곡 저장 또는 업데이트 후 ConcertSetlist 테이블에 추가
        int order = 1; // 곡의 순서를 저장하기 위한 변수
        for (String songTitle : songTitles) {
            // 곡 추가 또는 업데이트
            Song song = saveOrUpdateSong(pastConcert.getArtistId(), songTitle);

            // ConcertSetlist에 저장
            ConcertSetlist concertSetlist = ConcertSetlist.builder()
                    .pastConcert(pastConcert)  // PastConcert와 연관
                    .song(song)                // Song과 연관
                    .order(order++)            // 곡 순서
                    .build();

            // ConcertSetlist 저장
            concertSetlistRepository.save(concertSetlist);
        }
    }

    /**
     * 특정 아티스트의 PastConcert와 셋리스트를 조회합니다.
     *
     * @param artistId 아티스트 ID
     * @return 콘서트 및 셋리스트 정보
     */

    public List<PastConcertDTO> getSetlistsByArtistId(Long artistId) {
        List<PastConcert> pastConcerts = pastConcertRepository.findByArtistId_ArtistId(artistId);

        if (pastConcerts.isEmpty()) {
            throw new IllegalArgumentException("No concerts found for the given artist ID.");
        }

        return pastConcerts.stream().map(concert -> {
            PastConcertDTO concertDTO = new PastConcertDTO();
            concertDTO.setPastConcertId(concert.getPastConcertId());
            concertDTO.setVenueName(concert.getVenueName());
            concertDTO.setCityName(concert.getCityName());
            concertDTO.setDate(concert.getDate().toLocalDate().toString());

            List<SetlistDTO> setlists = concert.getSetlists().stream()
                    .map(setlist -> new SetlistDTO(setlist.getOrder(), setlist.getSongId().getTitle()))
                    .toList();

            concertDTO.setSetlists(setlists);
            return concertDTO;
        }).toList();
    }




    public PastConcert findPastConcertById(Long pastConcertId) {
        return pastConcertRepository.findById(pastConcertId)
                .orElseThrow(() -> new IllegalArgumentException("PastConcert with ID " + pastConcertId + " not found."));
    }


    public Song getSongByTitleAndArtistId(String title, Long artistId) {
        return songRepository.findByArtistIdAndTitle1(artistId, title)
                .orElseThrow(() -> new IllegalArgumentException("Song with title '" + title + "' not found for artist ID " + artistId));
    }





    @Transactional
    public void savePredictedSetlistToDatabase(NewConcert newConcert, List<Map<String, Object>> setlistJson) {
        // 곡 제목 목록 수집
        List<String> songTitles = setlistJson.stream()
                .map(songData -> (String) songData.get("title"))
                .collect(Collectors.toList());

        // 기존 곡들을 한 번의 쿼리로 조회하여 캐싱
        Map<String, Song> existingSongs = songRepository
                .findAllByArtistIdAndTitleIn(newConcert.getArtistId().getArtistId(), songTitles)
                .stream()
                .collect(Collectors.toMap(Song::getTitle, song -> song));

        // 순서를 위한 변수
        int order = 1;

        // 예상 셋리스트 저장
        for (Map<String, Object> songData : setlistJson) {
            String title = (String) songData.get("title");

            // 기존 곡 또는 새 곡 생성
            Song song = existingSongs.getOrDefault(title,
                    songRepository.save(
                            Song.builder()
                                    .artistId(newConcert.getArtistId())
                                    .title(title)
                                    .count(0)
                                    .ytLink(null) // YouTube 링크는 초기화
                                    .build()
                    ));

            // ConcertSetlist 생성 및 저장
            ConcertSetlist setlist = ConcertSetlist.builder()
                    .newConcert(newConcert) // NewConcert와 연관
                    .song(song)             // Song과 연관
                    .order(order++)         // 순서 저장
                    .build();

            concertSetlistRepository.save(setlist);
        }
    }




}

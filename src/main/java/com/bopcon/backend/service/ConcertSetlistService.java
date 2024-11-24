package com.bopcon.backend.service;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.ConcertSetlist;
import com.bopcon.backend.domain.PastConcert;
import com.bopcon.backend.domain.Song;
import com.bopcon.backend.dto.PastConcertDTO;
import com.bopcon.backend.dto.SetlistDTO;
import com.bopcon.backend.repository.PastConcertRepository;
import com.bopcon.backend.repository.SongRepository;
import com.bopcon.backend.repository.ConcertSetlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConcertSetlistService {

    private final PastConcertRepository pastConcertRepository;
    private final SongRepository songRepository;
    private final ConcertSetlistRepository concertSetlistRepository;

    @Autowired
    public ConcertSetlistService(PastConcertRepository pastConcertRepository,
                                 SongRepository songRepository,
                                 ConcertSetlistRepository concertSetlistRepository) {
        this.pastConcertRepository = pastConcertRepository;
        this.songRepository = songRepository;
        this.concertSetlistRepository = concertSetlistRepository;
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
     * @param artist 아티스트 ID
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
}

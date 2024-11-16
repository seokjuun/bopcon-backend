package me.shinsunyoung.service;

import me.shinsunyoung.entity.Song;
import me.shinsunyoung.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

    private final SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    // 노래 정보 저장
    public Song saveSong(String artistName, String title, String ytLink) {
        Song song = new Song(artistName, title, ytLink);
        return songRepository.save(song);
    }

    // 특정 아티스트 이름의 노래 목록 조회
    public List<Song> getSongsByArtistName(String artistName) {  // 메서드 이름과 파라미터 이름 변경
        return songRepository.findByArtistName(artistName);
    }
}

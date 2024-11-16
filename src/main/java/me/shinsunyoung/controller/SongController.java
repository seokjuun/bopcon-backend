package me.shinsunyoung.controller;

import me.shinsunyoung.entity.Song;
import me.shinsunyoung.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    // 새로운 노래 정보 추가
    @PostMapping("/add")
    public Song addSong(@RequestParam String artistName, @RequestParam String title, @RequestParam(required = false) String ytLink) {
        return songService.saveSong(artistName, title, ytLink);
    }

    // 특정 아티스트 이름의 노래 목록 조회
    @GetMapping("/{artistName}")
    public List<Song> getSongsByArtist(@PathVariable String artistName) {
        return songService.getSongsByArtistName(artistName);
    }
}

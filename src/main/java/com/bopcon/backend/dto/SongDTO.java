package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Song;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {
    private Long songId;    // 곡 ID
    private String title;   // 곡 제목
    private String lyrics;
    private String ytLink;  // YouTube 링크

    public static SongDTO fromEntity(Song song) {
        return new SongDTO(
                song.getSongId(),
                song.getTitle(),
                song.getLyrics(),
                song.getYtLink()
        );
    }
}



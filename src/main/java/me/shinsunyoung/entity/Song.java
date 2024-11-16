package me.shinsunyoung.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long songId;

    private String artistName;  // 아티스트 이름
    private String title;       // 노래 제목
    private String ytLink;      // 유튜브 링크

    public Song() {}

    public Song(String artistName, String title, String ytLink) {
        this.artistName = artistName;
        this.title = title;
        this.ytLink = ytLink;
    }

    // Getters and Setters
    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYtLink() {
        return ytLink;
    }

    public void setYtLink(String ytLink) {
        this.ytLink = ytLink;
    }
}

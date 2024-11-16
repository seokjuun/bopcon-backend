package me.shinsunyoung.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String venue;
    private String city;
    private String setlist;
    private String artistName; // 가수 이름 필드 추가

    public Concert() {}

    public Concert(String date, String venue, String city, String setlist, String artistName) {
        this.date = date;
        this.venue = venue;
        this.city = city;
        this.setlist = setlist;
        this.artistName = artistName; // 생성자에 추가
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSetlist() {
        return setlist;
    }

    public void setSetlist(String setlist) {
        this.setlist = setlist;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}

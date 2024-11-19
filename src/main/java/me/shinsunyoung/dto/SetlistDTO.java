package me.shinsunyoung.dto;

import java.util.ArrayList;
import java.util.List;

public class SetlistDTO {
    private List<Setlist> setlist;

    public List<Setlist> getSetlist() {
        return setlist != null ? setlist : new ArrayList<>();
    }

    public void setSetlist(List<Setlist> setlist) {
        this.setlist = setlist;
    }

    public static class Setlist {
        private String eventDate;
        private Venue venue;
        private Artist artist;
        private Sets sets;

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public Venue getVenue() {
            return venue != null ? venue : new Venue(); // Null 방지
        }

        public void setVenue(Venue venue) {
            this.venue = venue;
        }

        public Artist getArtist() {
            return artist != null ? artist : new Artist(); // Null 방지
        }

        public void setArtist(Artist artist) {
            this.artist = artist;
        }

        public Sets getSets() {
            return sets != null ? sets : new Sets(); // Null 방지
        }

        public void setSets(Sets sets) {
            this.sets = sets;
        }
    }

    public static class Venue {
        private String name;
        private City city;

        public String getName() {
            return name != null ? name : "Unknown Venue"; // 기본값 설정
        }

        public void setName(String name) {
            this.name = name;
        }

        public City getCity() {
            return city != null ? city : new City(); // Null 방지
        }

        public void setCity(City city) {
            this.city = city;
        }
    }

    public static class City {
        private String name;

        public String getName() {
            return name != null ? name : "Unknown City"; // 기본값 설정
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Artist {
        private String name;

        public String getName() {
            return name != null ? name : "Unknown Artist"; // 기본값 설정
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Sets {
        private List<Set> set;

        public List<Set> getSet() {
            return set != null ? set : new ArrayList<>(); // 빈 리스트 반환
        }

        public void setSet(List<Set> set) {
            this.set = set;
        }
    }

    public static class Set {
        private List<Song> song;

        public List<Song> getSong() {
            return song != null ? song : new ArrayList<>(); // 빈 리스트 반환
        }

        public void setSong(List<Song> song) {
            this.song = song;
        }
    }

    public static class Song {
        private String name;

        public String getName() {
            return name != null ? name : "Unknown Song"; // 기본값 설정
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

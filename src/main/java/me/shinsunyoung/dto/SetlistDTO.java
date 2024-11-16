package me.shinsunyoung.dto;

import java.util.List;

public class SetlistDTO {
    private String eventDate;
    private Venue venue;
    private Sets sets;
    private Artist artist; // 가수 정보 추가

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Sets getSets() {
        return sets;
    }

    public void setSets(Sets sets) {
        this.sets = sets;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public static class Artist {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Venue {
        private String name;
        private City city;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public City getCity() {
            return city;
        }

        public void setCity(City city) {
            this.city = city;
        }
    }

    public static class City {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Sets {
        private List<Set> set;

        public List<Set> getSet() {
            return set;
        }

        public void setSet(List<Set> set) {
            this.set = set;
        }
    }

    public static class Set {
        private List<Song> song;

        public List<Song> getSong() {
            return song;
        }

        public void setSong(List<Song> song) {
            this.song = song;
        }
    }

    public static class Song {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

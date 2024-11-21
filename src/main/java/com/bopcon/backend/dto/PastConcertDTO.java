package com.bopcon.backend.dto;

import com.bopcon.backend.dto.SetlistDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON에서 제외
@Data
public class PastConcertDTO {
    private Long pastConcertId;
    private String venueName;
    private String cityName;
    private String date;
    private List<SetlistDTO> setlists;

    public Long getPastConcertId() {
        return pastConcertId;
    }
    public void setPastConcertId(Long pastConcertId) {
        this.pastConcertId = pastConcertId;
    }

    public String getVenueName() {
        return venueName;
    }
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public List<SetlistDTO> getSetlists() {
        return setlists;
    }
    public void setSetlists(List<SetlistDTO> setlists) {
        this.setlists = setlists;
    }
}

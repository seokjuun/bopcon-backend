package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PredictSetlistDTO {
    private String songTitle;
    private Integer order;
    private String lyrics;
    private String ytLink;
}
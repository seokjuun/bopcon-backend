package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetlistDTO {
    private int order;
    private String songName;
}

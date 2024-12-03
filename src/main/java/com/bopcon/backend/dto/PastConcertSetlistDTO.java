package com.bopcon.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PastConcertSetlistDTO {
    private LocalDate date; // 콘서트 날짜
    private String songTitle; // 곡 이름
    private Integer order; // 곡 순서
}
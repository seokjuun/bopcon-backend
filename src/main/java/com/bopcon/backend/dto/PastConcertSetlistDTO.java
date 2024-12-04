package com.bopcon.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PastConcertSetlistDTO {
    @JsonFormat(pattern = "yyyy-MM-dd") // JSON 날짜 형식 지정
    private LocalDate date; // 콘서트 날짜
    private String songTitle; // 곡 이름
    private Integer order; // 곡 순서
}
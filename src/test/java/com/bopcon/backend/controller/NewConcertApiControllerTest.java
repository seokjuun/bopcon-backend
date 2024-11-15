package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.repository.NewConcertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성, 컨트롤러 테스트를 위한 MockMVC 객체 자동 구성
class NewConcertApiControllerTest {
    @Autowired
    protected MockMvc mockMvc; //HTTP 요청과 응답을 모의로 처리할 수 있는 객체

    @Autowired
    protected ObjectMapper objectMapper; // 직렬화(자바 객체를 json 데이터로 변환), 역직렬화(반대)를 위한 클래스

    @Autowired
    private WebApplicationContext context; // 현재의 Spring 애플리케이션 컨텍스트를 담고 있는 객체

    @Autowired
    NewConcertRepository newConcertRepository;

    @BeforeEach // 테스트 실행 전 실행하는 메서드
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        newConcertRepository.deleteAll();
    }

    @DisplayName("addNewConcert: 새 내한 콘서트 추가에 성공.")
    @Test
    public void addNewConcert() throws Exception {
        // given
        final String url = "/api/admin/new-concert";
        final Artist artistId = null;
        final String title = "Upcoming concert";
        final String subTitle = "subtitle";
        final LocalDate date = LocalDate.now();
        final String venueName = "Venue C";
        final String cityName = "City C";
        final String countryName = "Country C";
        final String countryCode = "CC";
        final String ticketPlatforms = "인터파크, 티켓링크";
        final String ticketUrl = "https://example.com/ticket";
        final String posterUrl = "https://example.com/poster";
        final String genre = "POP";
        final NewConcert.ConcertStatus concertStatus = NewConcert.ConcertStatus.valueOf("UPCOMING");

        final AddNewConcertRequest userRequest = new AddNewConcertRequest(
                artistId.getArtistId(), title, subTitle, date, venueName, cityName, countryName,
                countryCode, ticketPlatforms, ticketUrl, posterUrl, genre, concertStatus);

        final String requestBody = objectMapper.writeValueAsString(userRequest); // dto 객체 만들고 json 으로 직렬화 시킴


        // when
        // MockMvc 를 사용해 HTTP 메서드, URL, 요청 본문, 요청 타입 등을 설정 후, 테스트 요청 보냄
        // ResultActions : 테스트에서 HTTP 요청의 결과를 다루기 위한 객체, MockMvc.perform()의 결과로 반환됨
        ResultActions result = mockMvc.perform(post(url) // perform() : HTTP 요청을 모의로 수행
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody)); // JSON 데이터가 서버로 전송

        // then
        result.andExpect(status().isCreated()); // 201 상태 코드가 반환되는지 검증
        List<NewConcert> newConcerts = newConcertRepository.findAll(); // DB에 저장된 모든 콘서트 객체를 담은 리스트 생성

        // 필드별 검증
        NewConcert savedConcert = newConcerts.get(0);
        assertThat(savedConcert.getTitle()).isEqualTo(title); // 제목 검증
        assertThat(savedConcert.getSubTitle()).isEqualTo(subTitle);
        assertThat(savedConcert.getDate()).isEqualTo(date); // 날짜 검증
        assertThat(savedConcert.getVenueName()).isEqualTo(venueName); // 공연장 검증
        assertThat(savedConcert.getCityName()).isEqualTo(cityName);
        assertThat(savedConcert.getCountryName()).isEqualTo(countryName);
        assertThat(savedConcert.getCountryCode()).isEqualTo(countryCode);
        assertThat(savedConcert.getTicketPlatforms()).isEqualTo(ticketPlatforms);
        assertThat(savedConcert.getTicketUrl()).isEqualTo(ticketUrl);
        assertThat(savedConcert.getPosterUrl()).isEqualTo(posterUrl);
        assertThat(savedConcert.getGenre()).isEqualTo(genre);
        assertThat(savedConcert.getConcertStatus()).isEqualTo(concertStatus);
    }
}
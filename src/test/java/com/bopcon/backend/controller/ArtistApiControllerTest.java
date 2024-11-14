package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.dto.AddArtistRequest;
import com.bopcon.backend.repository.ArtistRepository;
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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성, 컨트롤러 테스트를 위한 MockMVC 객체 자동 구성
class ArtistApiControllerTest {

        @Autowired
        protected MockMvc mockMvc; //HTTP 요청과 응답을 모의로 처리할 수 있는 객체

        @Autowired
        protected ObjectMapper objectMapper; // 직렬화(자바 객체를 json 데이터로 변환), 역직렬화(반대)를 위한 클래스

        @Autowired
        private WebApplicationContext context; // 현재의 Spring 애플리케이션 컨텍스트를 담고 있는 객체

        @Autowired
        ArtistRepository artistRepository;

        @BeforeEach // 테스트 실행 전 실행하는 메서드
        public void mockMvcSetUp(){
            this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
            artistRepository.deleteAll();
        }

        @DisplayName("addArtist: 새 아티스트 등록 완료")
        @Test
        public void addArtist() throws Exception {
            // given
            final String url = "/api/admin/artists";
            final String mbid = "mbid-123";
            final String name = "artist_name";
            final String imgUrl = "http://image.com";
            final String snsLink = "http://sns.com";
            final String meidaLink = "http://meida.com";

            final AddArtistRequest addArtistRequest = new AddArtistRequest(
                    mbid, name, imgUrl, snsLink, meidaLink  );

            final String requestBody = objectMapper.writeValueAsString(addArtistRequest); // dto 객체 만들고 json 으로 직렬화 시킴

            // when
            // MockMvc 를 사용해 HTTP 메서드, URL, 요청 본문, 요청 타입 등을 설정 후, 테스트 요청 보냄
            // ResultActions : 테스트에서 HTTP 요청의 결과를 다루기 위한 객체, MockMvc.perform()의 결과로 반환됨
            ResultActions result = mockMvc.perform(post(url) // perform() : HTTP 요청을 모의로 수행
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(requestBody)); // JSON 데이터가 서버로 전송

            // then
            result.andExpect(status().isCreated()); // 201 상태 코드가 반환되는지 검증
            List<Artist> Artists = artistRepository.findAll(); // DB에 저장된 모든 콘서트 객체를 담은 리스트 생성

            // 필드별 검증
            Artist savedArtist = Artists.get(0);
            assertThat(savedArtist.getMbid()).isEqualTo(mbid);
            assertThat(savedArtist.getName()).isEqualTo(name);
            assertThat(savedArtist.getImgUrl()).isEqualTo(imgUrl);
            assertThat(savedArtist.getSnsUrl()).isEqualTo(snsLink);
            assertThat(savedArtist.getMediaUrl()).isEqualTo(meidaLink);

        }
}

package com.bopcon.backend.controller;


import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.dto.NewConcertResponse;
import com.bopcon.backend.dto.UpdateNewConcertRequest;
import com.bopcon.backend.service.NewConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor // final 필드나 @NotNull 이 붙은 필드들을 포함한 생성자를 자동 생성
@RestController // HTTP Response Body 에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class NewConcertApiController {
    private final NewConcertService newConcertService;
    // 등록
    @PostMapping("/api/admin/new-concert")
    public ResponseEntity<NewConcert> addNewConcert(@RequestBody AddNewConcertRequest request) {
        NewConcert savedNewConcert = newConcertService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNewConcert);
    }

    // 수정
    @PutMapping("/api/admin/new-concert/{concertId}")
    public ResponseEntity<NewConcert> updateNewConcert(@PathVariable long concertId, @RequestBody UpdateNewConcertRequest request){
        NewConcert updateNewconcert = newConcertService.update(concertId, request);
        return ResponseEntity.ok().body(updateNewconcert); //응답 값은 body 에 담아 전송
    }

    // 새 콘서트 전체 조회, 장르별로 필터링 가능
    @GetMapping("/api/new-concerts")  // /api/new-concerts?genre={장르}
    public ResponseEntity<List<NewConcertResponse>> findAllNewConcerts(@RequestParam(required = false) String genre) {
        List<NewConcertResponse> newConcerts;
        if(genre != null){
            newConcerts = newConcertService.findNewConcertsByGenre(genre)
                    .stream()
                    .map(NewConcertResponse::new)
                    .toList();
        } else {
            newConcerts = newConcertService.findAllNewConcerts()
                    .stream()
                    .map(NewConcertResponse::new)
                    .toList();
        }
        return ResponseEntity.ok()
                .body(newConcerts);
        // GET 요청이 오면 findAll() 메서드를 호출 후 응답용 객체인 ArticleResponse 로 파싱해 body 에 담아 클라이언트에 전송
    }

    // 새 콘서트 정보 조회
    @GetMapping("/api/new-concerts/{concertId}")
    public ResponseEntity<NewConcertResponse> findNewConcert(@PathVariable long concertId)
    {
        NewConcert newConcert = newConcertService.findByConcertId(concertId);
        return ResponseEntity.ok().body(new NewConcertResponse(newConcert));
    }


    // 새 콘서트 삭제
    @DeleteMapping("/api/new-concerts/{concertId}")
    public ResponseEntity<Void> deleteNewConcert(@PathVariable long concertId){
        newConcertService.delete(concertId);

        return ResponseEntity.ok().build(); //Http 응답 생성, build()는 본문이 없는 응답 생성
    }
}

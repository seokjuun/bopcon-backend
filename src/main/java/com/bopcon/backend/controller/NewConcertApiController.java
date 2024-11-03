package com.bopcon.backend.controller;


import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.dto.UpdateNewConcertRequest;
import com.bopcon.backend.service.NewConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("/api/admin/update-new-concert/{concertId}")
    public ResponseEntity<NewConcert> updateNewConcert(@PathVariable long concertId, @RequestBody UpdateNewConcertRequest request){
        NewConcert updateNewconcert = newConcertService.update(concertId, request);
        return ResponseEntity.ok().body(updateNewconcert); //응답 값은 body 에 담아 전송
    }
}

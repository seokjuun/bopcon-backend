package com.bopcon.backend.controller;


import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.service.NewConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor // final 필드나 @NotNull 이 붙은 필드들을 포함한 생성자를 자동 생성
@RestController // HTTP Response Body 에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class NewConcertApiController {
    private final NewConcertService newConcertService;

    @PostMapping("/api/admin/new-concert")
    public ResponseEntity<NewConcert> addNewConcert(@RequestBody AddNewConcertRequest request) {
        NewConcert savedNewConcert = newConcertService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNewConcert);
    }
}

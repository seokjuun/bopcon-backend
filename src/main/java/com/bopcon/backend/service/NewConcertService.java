package com.bopcon.backend.service;


import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.repository.NewConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NewConcertService {
    private final NewConcertRepository newConcertRepository;

    // 뉴 콘서트 추가 메서드
    public NewConcert save(AddNewConcertRequest request) {
        return newConcertRepository.save(request.toNewConcert());
    }
}

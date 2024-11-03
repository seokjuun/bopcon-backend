package com.bopcon.backend.service;


import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.dto.AddNewConcertRequest;
import com.bopcon.backend.dto.UpdateNewConcertRequest;
import com.bopcon.backend.repository.NewConcertRepository;
import jakarta.transaction.Transactional;
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

    // 뉴 콘서트 수정
    @Transactional
    public NewConcert update(long newConcertId, UpdateNewConcertRequest request){
        NewConcert newConcert = newConcertRepository.findById(newConcertId)
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ newConcertId));

        newConcert.updateNewConcert(request);
        return newConcert;
    }
}
// request.getArtistId(), request.getTitle(), request.getSubTitle(), request.getDate(),
//                request.getVenueName(), request.getCityName(), request.getCountryName(), request.getCountryCode(),
//                request.getTicketPlatforms(), request.getTicketUrl(), request.getPosterUrl(),
//                request.getGenre(), request.getConcertStatus()
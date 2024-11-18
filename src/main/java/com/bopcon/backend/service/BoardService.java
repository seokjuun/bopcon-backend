package com.bopcon.backend.service;

import com.bopcon.backend.domain.Board;
import com.bopcon.backend.dto.AddBoardRequest;
import com.bopcon.backend.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // final 이 붙거나 @NotNull 이 붙은 필드의 생성자 추가
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public Board save(AddBoardRequest request){
        return boardRepository.save(request.toEntity());
    }

    public Board findById(long id){
        return boardRepository.findById(id) //Optional<Article> 타입으로 반환, 객체가 존재할 수 도 있고, 없을 수도 있어서
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ id));
        //orElseThrow() : Optional 객체가 값을 가지고 있지 않을 때, 예외를 던지는 메서드
        // 값이 없을 때 (잘못된인자) 던질 예외를 람다로 정의
    }
}

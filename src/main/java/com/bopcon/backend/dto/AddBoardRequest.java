package com.bopcon.backend.dto;

import com.bopcon.backend.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddBoardRequest {
    private String title;
    private String content;

    // 그니까 사실 롬복으로 생성한 기본생성자랑, 매개값 가지는 생성자가 있는 거임 ㅇㅇ

    // Article 타입의 toEntity 메서드 정의
    public Board toEntity(){ // 생성자를 사용해 객체 생성 반환타입 Article
        return Board.builder()
                .title(title)
                .content(content)
                .build(); // 설정된 값을 가진 객체를 생성하고 반환

        // 그니까 이 메서드는 호출할때 빌더 패턴을 사용해 DTO 를 엔티티(Article 타입 객체)로 만들어준다는 거임 ㅇㅇ
        // DTO : 단순히 계층끼리 데이터를 교환하기 위해 사용하는 객체
        // 엔티티 : 특정한 의미나 정체성을 가지는 데이터 베이스, 고유 식별자, 속성, 관계 등을 가짐
    }
}

package com.bobcon.backend.dto;


import com.bobcon.backend.domain.Artist;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArtistRequest {
    private String mbid;
    private String name;
    private String imgUrl;
    private String snsUrl;
    private String mediaUrl;

    // DTO 를 엔티티(Artist 타입 객체)로
    public Artist toEntity() {
        return Artist.builder()
                .mbid(mbid)
                .name(name)
                .imgUrl(imgUrl)
                .snsUrl(snsUrl)
                .mediaUrl(mediaUrl)
                .build();
    }
}

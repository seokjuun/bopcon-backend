package com.bopcon.backend.service;

import com.bopcon.backend.domain.Article;
import com.bopcon.backend.domain.Artist;
import com.bopcon.backend.domain.NewConcert;
import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.AddArticleRequest;
import com.bopcon.backend.dto.UpdateArticleRequest;
import com.bopcon.backend.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor // final 이 붙거나 @NotNull 이 붙은 필드의 생성자 추가
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final ArtistService artistService; // Artist 조회를 위한 서비스
    private final UserService userService; // User 조회를 위한 서비스
    private final NewConcertService newConcertService; // NewConcert 조회를 위한 서비스


    //글 추가 메서드
    public Article save(AddArticleRequest request){
        Artist artist = artistService.findByArtistId(request.getArtistId());
        User user = userService.findById(request.getUserId());
        NewConcert newConcert = null;
        if (request.getCategoryType() == Article.CategoryType.NEW_CONCERT) {
            newConcert = newConcertService.findByConcertId(request.getNewConcertId());
        }
        return boardRepository.save(request.toEntity(artist, user, newConcert));
    }

    //글 목록 가져오기
    public List<Article> findAll(){
        return boardRepository.findAll();
    }

    // 글 조회 : ID 이용
    public Article findById(long id){
        return boardRepository.findById(id) //Optional<Article> 타입으로 반환, 객체가 존재할 수 도 있고, 없을 수도 있어서
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ id));
        //orElseThrow() : Optional 객체가 값을 가지고 있지 않을 때, 예외를 던지는 메서드
        // 값이 없을 때 (잘못된인자) 던질 예외를 람다로 정의
    }

    // 글 조회 : 아티스트 id 이용
    public List<Article> findByArtist(long artistId){

        return boardRepository.findByArtistArtistId(artistId);
    }

    // 글 삭제 : ID를 받은 뒤, deleteByID() 메서드로 디비에서 데이터 삭제
    public void delete(long id){
        boardRepository.deleteById(id);
    }

    // 글 수정
    @Transactional //트랜잭션 메서드
    public Article update(long id, UpdateArticleRequest request){
        Article article = boardRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found: "+ id));

        NewConcert newConcert = null;
        if ("NEW_CONCERT".equals(request.getCategoryType())) {
            newConcert = newConcertService.findByConcertId(request.getNewConcertId());
        }


        article.update(request.getTitle(), request.getContent(), Article.CategoryType.valueOf(request.getCategoryType()),
                newConcert);
        return article;
    }
}

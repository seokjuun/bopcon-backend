package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Article;
import com.bopcon.backend.domain.User;
import com.bopcon.backend.dto.AddArticleRequest;
import com.bopcon.backend.dto.AddArticleResponse;
import com.bopcon.backend.dto.ArticleResponse;
import com.bopcon.backend.dto.UpdateArticleRequest;
import com.bopcon.backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor // final 필드나 @NotNull 이 붙은 필드들을 포함한 생성자를 자동 생성
@RestController // HTTP Response Body 에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BoardApiController {
    private final BoardService boardService;

    //글 등록
    @PostMapping("/api/articles")
    public ResponseEntity<AddArticleResponse> addArticle(
            @RequestBody AddArticleRequest request,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        Article savedArticle = boardService.save(request, user);
        AddArticleResponse response = new AddArticleResponse(savedArticle);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //글 목록 조회
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = boardService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    //글 조회
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id)
    {
        Article article = boardService.findById(id);
        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    // 특정 아티스트 게시물 조회
    @GetMapping("/api/articles/artist/{id}")
    public ResponseEntity<List<ArticleResponse>> findArtistArticles(@PathVariable long id) {
        List<ArticleResponse> articles = boardService.findByArtist(id)
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.ok().body(articles);
    }

    // 특정 유저 게시물 조회
    @GetMapping("/api/articles/user")
    public ResponseEntity<List<ArticleResponse>> findUserArticles(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }

        List<ArticleResponse> articles = boardService.findArticlesByUser(user);
        return ResponseEntity.ok().body(articles);

    }

    // 글 삭제
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        boardService.delete(id);

        return ResponseEntity.ok().build(); //Http 응답 생성, build()는 본문이 없는 응답 생성
    }

    // 글 수정
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<AddArticleResponse> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        if (!request.isValid()) {
            return ResponseEntity.badRequest().build(); // 유효하지 않은 요청 처리
        }

        Article updateArticle = boardService.update(id, request);
        AddArticleResponse response = new AddArticleResponse(updateArticle);
        return ResponseEntity.ok().body(response); //응답 값은 body 에 담아 전송
    }
}

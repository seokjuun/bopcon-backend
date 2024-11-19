package com.bopcon.backend.controller;

import com.bopcon.backend.domain.Article;
import com.bopcon.backend.dto.AddArticleRequest;
import com.bopcon.backend.dto.AddArticleResponse;
import com.bopcon.backend.dto.ArticleResponse;
import com.bopcon.backend.dto.UpdateArticleRequest;
import com.bopcon.backend.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor // final 필드나 @NotNull 이 붙은 필드들을 포함한 생성자를 자동 생성
@RestController // HTTP Response Body 에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BoardApiController {
    private final BoardService boardService;
    @PostMapping("/api/articles") //HTTP 메서드가 POST 일 때 전달받은 URL 과 동일하면 메서드(addArticle)로 매핑
    // @RequestBody 로 요청 본문 값 매핑
    public ResponseEntity<AddArticleResponse> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = boardService.save(request);
        AddArticleResponse response = new AddArticleResponse(savedArticle);
        //요청한 자원이 성공적으로 생성되었으며 저장된 블로그 글 정보를 응답 객체에 담아 전송
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
    @GetMapping("/api/articles/{id}") // URL 에서 {id}에 해당하는 값이 id 로 들어옴
    //url 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id)
    {
        Article article = boardService.findById(id); // 엔티티 조회, 없으면 예외

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }
    // @PathVariable : URL 에서 값을 가져오는 애너테이션, 여기선 id 값

    // 글 삭제
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        boardService.delete(id);

        return ResponseEntity.ok().build(); //Http 응답 생성, build()는 본문이 없는 응답 생성
    }

    // 글 수정
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        if (!request.isValid()) {
            return ResponseEntity.badRequest().build(); // 유효하지 않은 요청 처리
        }

        Article updateArticle = boardService.update(id, request);
        return ResponseEntity.ok().body(updateArticle); //응답 값은 body 에 담아 전송
    }
}

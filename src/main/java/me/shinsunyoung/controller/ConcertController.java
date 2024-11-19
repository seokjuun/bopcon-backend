package me.shinsunyoung.controller;

import me.shinsunyoung.dto.SetlistDTO;
import me.shinsunyoung.service.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ConcertController {

    @Autowired
    private ConcertService concertService;

    // 전체 콘서트 목록 페이지
    @GetMapping("/concerts")
    public String getAllConcerts(Model model) {
        // API에서 데이터를 가져옵니다.
        SetlistDTO setlistDTO = concertService.fetchConcertData();
        List<SetlistDTO.Setlist> concerts = setlistDTO.getSetlist();

        model.addAttribute("concerts", concerts); // 전체 콘서트 리스트 전달
        return "concertList"; // concertList.html 템플릿 렌더링
    }

    // 특정 콘서트 상세 페이지
    @GetMapping("/concerts/{id}")
    public String getConcertById(@PathVariable int id, Model model) {
        // API에서 데이터를 가져옵니다.
        SetlistDTO setlistDTO = concertService.fetchConcertData();
        List<SetlistDTO.Setlist> concerts = setlistDTO.getSetlist();

        // ID가 범위를 벗어나면 에러 페이지를 보여줍니다.
        if (id < 1 || id > concerts.size()) {
            model.addAttribute("error", "해당 콘서트를 찾을 수 없습니다.");
            return "errorView"; // errorView.html 템플릿 렌더링
        }

        // 콘서트 데이터를 모델에 추가합니다.
        model.addAttribute("concert", concerts.get(id - 1)); // 1-based index 처리
        model.addAttribute("currentId", id); // 현재 콘서트 ID 전달
        model.addAttribute("totalConcerts", concerts.size()); // 총 콘서트 개수 전달
        return "concertDetail"; // concertDetail.html 템플릿 렌더링
    }
}

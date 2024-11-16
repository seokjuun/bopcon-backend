package me.shinsunyoung.controller;

import me.shinsunyoung.entity.Concert;
import me.shinsunyoung.service.SetlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SetlistController {

    private final SetlistService setlistService;

    @Autowired
    public SetlistController(SetlistService setlistService) {
        this.setlistService = setlistService;
    }

    @GetMapping("/setlist")
    public String getSetlist(Model model) {
        List<Concert> setlist = setlistService.getSetlist(); // 데이터베이스에서 가져온 데이터를 List로 받음
        model.addAttribute("setlist", setlist);
        return "setlistView";
    }
}

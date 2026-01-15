package com.example.project_board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 현재 들어온 요청을 /articles로 토스 (주소창엔 / 로만 표시)
@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "forward:/articles";
    }
}

package com.example.project_board.controller;

import com.example.project_board.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 기본적으로 WebMvcTest는 Controller만 떼어내서 테스트하려고 함. @Configuration 무시.
@Import(SecurityConfig.class)
@WebMvcTest(MainController.class)           // MainController만 테스트
class MainControllerTest {

    private final MockMvc mvc;              // 서버 구동X, 핵심 로직만 테스트

    public MainControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void givenNothing_whenRequestingRootPage_thenRedirectsToArticlePage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("forward:/articles"))
                .andExpect(forwardedUrl("/articles"))
                .andDo(MockMvcResultHandlers.print());
    }
}
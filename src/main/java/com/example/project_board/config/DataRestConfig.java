package com.example.project_board.config;

import com.example.project_board.domain.Article;
import com.example.project_board.domain.ArticleComment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class DataRestConfig {

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return RepositoryRestConfigurer.withConfig((config, cors) -> {
            // 1. 여기서 경로를 직접 박아버립니다.
            config.setBasePath("/api");

            // 2. (선택사항) ID를 응답에 포함하고 싶을 때 사용
            config.exposeIdsFor(Article.class, ArticleComment.class);
        });
    }
}
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
            config.setBasePath("/api");

            config.exposeIdsFor(Article.class, ArticleComment.class);
        });
    }
}
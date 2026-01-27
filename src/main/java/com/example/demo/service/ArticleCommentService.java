package com.example.demo.service;

import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.repository.ArticleCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;

    public ArticleCommentService(ArticleCommentRepository articleCommentRepository) {
        this.articleCommentRepository = articleCommentRepository;
    }

    public Page<ArticleCommentDto> searchArticleComments(Long articleId, Pageable pageable) {
        return articleCommentRepository
                .findByArticleId(articleId, pageable)
                .map(ArticleCommentDto::from);
    }
}

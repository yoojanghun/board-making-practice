package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.ArticleComment;
import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.repository.ArticleCommentRepository;
import com.example.demo.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;

    public ArticleCommentService(ArticleCommentRepository articleCommentRepository, ArticleRepository articleRepository) {
        this.articleCommentRepository = articleCommentRepository;
        this.articleRepository = articleRepository;
    }

    @Transactional(readOnly = true)
    public Page<ArticleCommentDto> searchArticleComments(Long articleId, Pageable pageable) {
        return articleCommentRepository
                .findByArticleId(articleId, pageable)
                .map(ArticleCommentDto::from);
    }

    public ArticleCommentDto saveArticleComment(ArticleCommentDto articleCommentDto) {
        Article article = articleRepository.findById(articleCommentDto.articleId())
                .orElseThrow(EntityNotFoundException::new);

        ArticleComment articleComment = articleCommentDto.toEntity(article);

        if(articleCommentDto.parentCommentId() != null) {
            articleComment.setParentCommentId(articleCommentDto.parentCommentId());
        }
        ArticleComment savedArticleComment = articleCommentRepository.save(articleComment);

        return ArticleCommentDto.from(savedArticleComment);
    }

    public void deleteArticleComment(String userId, Long commentId) {
        ArticleComment articleComment = articleCommentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);
        if(articleComment.getUserAccount().getUserId().equals(userId)) {
            articleCommentRepository.delete(articleComment);
        } else{
            throw new SecurityException("댓글 삭제 권한이 없습니다.");
        }
    }
}

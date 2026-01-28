package com.example.demo.controller;

import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.dto.request.ArticleCommentRequest;
import com.example.demo.dto.response.ArticleCommentResponse;
import com.example.demo.dto.security.BoardPrincipal;
import com.example.demo.service.ArticleCommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/comments")
@RestController
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    public ArticleCommentController(ArticleCommentService articleCommentService) {
        this.articleCommentService = articleCommentService;
    }

    @GetMapping("/{articleId}")
    public List<ArticleCommentResponse> searchArticleComments(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @PathVariable Long articleId) {
        Page<ArticleCommentDto> articleCommentDtos = articleCommentService.searchArticleComments(articleId, pageable);

        Page<ArticleCommentResponse> articleCommentResponses = articleCommentDtos.map(ArticleCommentResponse::from);

        return articleCommentResponses.getContent();
    }

    @PostMapping("/new")
    public ArticleCommentResponse postNewArticleComment(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            @RequestBody ArticleCommentRequest articleCommentRequest
    ) {
        UserAccountDto userAccountDto = boardPrincipal.toDto();
        ArticleCommentDto articleCommentDto = articleCommentRequest.toDto(userAccountDto);
        ArticleCommentDto returnedArticleCommentDto = articleCommentService.saveArticleComment(articleCommentDto);

        return ArticleCommentResponse.from(returnedArticleCommentDto);
    }

}

package com.example.demo.controller;

import com.example.demo.domain.constant.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.dto.request.ArticleRequest;
import com.example.demo.dto.response.ArticleResponse;
import com.example.demo.dto.response.ArticleWithCommentsResponse;
import com.example.demo.dto.security.BoardPrincipal;
import com.example.demo.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/articles")
@RestController
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 전체 게시글 목록 조회
    @GetMapping
    public List<ArticleResponse> searchArticles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ArticleDto> articleDtos = articleService
                .searchArticles(searchType, searchValue, pageable);

        Page<ArticleResponse> articleResponse = articleDtos.map(ArticleResponse::from);

        return articleResponse.getContent();
    }

    // 하나의 게시글 클릭 시 게시글과 댓글이 모두 보이도록
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleWithCommentsResponse> searchArticleById(
            @PathVariable Long articleId
    ) {
        ArticleWithCommentsDto articleWithCommentsDto = articleService.getArticleWithComments(articleId);
        return ResponseEntity.ok(ArticleWithCommentsResponse.from(articleWithCommentsDto));
    }

    @PostMapping("/form")
    public ArticleResponse postNewArticle(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            @RequestBody ArticleRequest articleRequest
    ) {
        UserAccountDto userAccountDto = boardPrincipal.toDto();
        ArticleDto articleDto = articleRequest.toDto(userAccountDto);
        ArticleDto returnedArticleDto = articleService.saveArticle(articleDto);

        return ArticleResponse.from(returnedArticleDto);
    }

    @GetMapping("/{articleId}/form")
    public ArticleResponse getArticle(@PathVariable Long articleId) {
        ArticleDto articleDto = articleService.getArticle(articleId);

        return ArticleResponse.from(articleDto);
    }

    @PutMapping("/{articleId}/form")
    public ArticleResponse updateArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            @RequestBody ArticleRequest articleRequest
    ) {
        UserAccountDto userAccountDto = boardPrincipal.toDto();
        ArticleDto articleDto = articleRequest.toDto(userAccountDto);
        ArticleDto updatedArticle = articleService.updateArticle(articleId, articleDto);

        return ArticleResponse.from(updatedArticle);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        UserAccountDto userAccountDto = boardPrincipal.toDto();
        String userId = userAccountDto.userId();
        articleService.deleteArticle(articleId, userId);
        return ResponseEntity.noContent().build();      // 204 반환
    }
}

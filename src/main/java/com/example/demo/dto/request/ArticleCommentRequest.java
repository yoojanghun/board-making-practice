package com.example.demo.dto.request;

import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.UserAccountDto;

public record ArticleCommentRequest(
        Long articleId,
        Long parentCommentId,
        String content
) {
    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(this.articleId, userAccountDto, this.parentCommentId, this.content);
    }
}

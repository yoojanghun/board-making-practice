package com.example.demo.dto;

import com.example.demo.domain.ArticleComment;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id,
        Long articleId,
        UserAccountDto userAccountDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleCommentDto of(Long id, Long articleId, UserAccountDto userAccountDto, Long parentCommentId, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDto(id, articleId, userAccountDto, parentCommentId ,content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto from(ArticleComment entity) {
        return new ArticleCommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getParentCommentId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
}

// ArticleCommentDto 내부에 "Set<ArticleCommentDto> childCommentsDto"를 생성한 후,
// from 메서드에서 entity.getChildComments().stream.map(ArticleCommentDto::from) 해 버리면
// 재귀 호출이 많이 일어날 수 있다. => 무한 대댓글 달릴 시 성능 저하

// 위 코드에서 모든 ArticleComment entity에 접근해서 parentCommentId 저장 (대댓글, 대대댓글, 대대대댓글 등 모든 comment에 접근)
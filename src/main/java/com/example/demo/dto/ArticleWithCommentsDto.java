package com.example.demo.dto;

import com.example.demo.domain.Article;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsDto(
        Long id,                                // article의 id
        UserAccountDto userAccountDto,
        String title,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy,
        Set<ArticleCommentDto> articleCommentDtos,
        Set<HashtagDto> hashtagDtos
) {
    public static ArticleWithCommentsDto of(Long id, UserAccountDto userAccountDto, String title, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, Set<ArticleCommentDto> articleCommentDtos, Set<HashtagDto> hashtagDtos) {
        return new ArticleWithCommentsDto(id, userAccountDto, title, content, createdAt, createdBy, modifiedAt, modifiedBy, articleCommentDtos, hashtagDtos);
    }

    public static ArticleWithCommentsDto from(Article entity) {
        return new ArticleWithCommentsDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy(),
                entity.getArticleComments()
                        .stream()
                        .map(ArticleCommentDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),           // 댓글 순서를 유지해야 해서 LinkedHashSet 사용
                entity.getArticleHashtags()
                        .stream()
                        .map(articleHashtag ->
                                HashtagDto.from(articleHashtag.getHashtag()))
                        .collect(Collectors.toUnmodifiableSet())
        );
    }
}

// entity.getArticleComments()는 해당 Article에 속한 직접적인 댓글(Top-level comments)들만 가져온다.
// 대댓글, 대대댓글은 ArticleComment entity에 정의되어 있다.
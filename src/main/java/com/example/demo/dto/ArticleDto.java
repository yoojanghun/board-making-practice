package com.example.demo.dto;

import com.example.demo.domain.Article;
import com.example.demo.domain.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        Set<HashtagDto> hastagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    // 처음 ArticleDto 생성할 때 (ArticleRequest -> ArticleDto)
    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, Set<HashtagDto> hashtagDtos) {
        return new ArticleDto(null, userAccountDto, title, content, hashtagDtos, null, null, null, null);
    }

    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getArticleHashtags()
                        .stream()
                        .map(articleHashtag
                                -> HashtagDto.from(articleHashtag.getHashtag()))
                        .collect(Collectors.toUnmodifiableSet()),       // 읽기 전용 Set으로 만듦
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Article toEntity(UserAccount userAccount) {
        return Article.of(
                userAccount,
                this.title(),
                this.content()
        );
    }
}

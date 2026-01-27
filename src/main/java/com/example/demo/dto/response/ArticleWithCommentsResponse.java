package com.example.demo.dto.response;

import com.example.demo.dto.ArticleCommentDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.dto.HashtagDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<ArticleCommentResponse> articleCommentsResponse
) {
    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags, LocalDateTime createdAt, String email, String nickname, String userId, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtags, createdAt, email, nickname, userId, articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if(nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos()
                        .stream()
                        .map(HashtagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.createdAt(),
                dto.userAccountDto().email(),
                nickname,
                dto.userAccountDto().userId(),
                organizeChildComments(dto.articleCommentDtos())
        );
    }

    // ArticleCommentDto는 모든 ArticleComment entity들을 dto로 변환한 것.
    public static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {
        Map<Long, ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));        // Function.identity()는 입력받은 값 그대로 내보냄

        // 아래 코드에서 articleCommentResponse에서 childComments들을 TreeSet에 add함.
        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment)
                .forEach(articleCommentResponse -> {
                    ArticleCommentResponse parentCommentResponse = map.get(articleCommentResponse.parentCommentId());
                    parentCommentResponse.childComments().add(articleCommentResponse);
                });

        // articleCommentResponse들이 각각 가지고 있는 자식들을 알게 됨.
        return map.values()
                .stream().filter(articleCommentResponse -> !articleCommentResponse.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::createdAt)
                                .reversed()
                                .thenComparing(ArticleCommentResponse::id)
                    ))
                );
    }
}

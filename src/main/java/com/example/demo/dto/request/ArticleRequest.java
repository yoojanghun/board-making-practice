package com.example.demo.dto.request;

import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.HashtagDto;
import com.example.demo.dto.UserAccountDto;

import java.util.Set;
import java.util.stream.Collectors;

public record ArticleRequest(
        String title,
        String content,
        Set<String> hashtagNames
) {
    public static ArticleRequest of(String title, String content, Set<String> hashtagNames) {
        return new ArticleRequest(title, content, hashtagNames);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto) {
        Set<HashtagDto> hashtagDtos = hashtagNames
                .stream()
                .map(HashtagDto::of)
                .collect(Collectors.toUnmodifiableSet());

        return ArticleDto.of(userAccountDto, title, content, hashtagDtos);
    }
}

// UserAccountDto와 ArticleRequest를 받아서 ArticleDto로 변환해야 함.
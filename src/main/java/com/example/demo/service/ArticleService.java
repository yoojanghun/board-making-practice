package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.Hashtag;
import com.example.demo.domain.UserAccount;
import com.example.demo.domain.constant.SearchType;
import com.example.demo.dto.ArticleDto;
import com.example.demo.dto.ArticleWithCommentsDto;
import com.example.demo.dto.HashtagDto;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.HashtagRepository;
import com.example.demo.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    public ArticleService(ArticleRepository articleRepository, UserAccountRepository userAccountRepository,
                          HashtagRepository hashtagRepository) {
        this.articleRepository = articleRepository;
        this.userAccountRepository = userAccountRepository;
        this.hashtagRepository = hashtagRepository;
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles (SearchType searchType, String searchValue, Pageable pageable) {
        if(searchValue == null || searchValue.isBlank()) {                          // isBlank(): 공백만 있을 때 또는 "".
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContainingIgnoreCase(searchValue, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContainingIgnoreCase(searchValue, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContainingIgnoreCase(searchValue, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContainingIgnoreCase(searchValue, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagNames(
                            Arrays.stream(searchValue.split(" ")).toList(),
                            pageable).map(ArticleDto::from);
        };
    }


    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(EntityNotFoundException::new);
        return ArticleWithCommentsDto.from(article);
    }

    public ArticleDto saveArticle(ArticleDto articleDto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(articleDto.userAccountDto().userId());
        Article article = articleDto.toEntity(userAccount);

        Set<Hashtag> hashtags = renewHashtagsFromContent(articleDto);
        article.addHashtags(hashtags);

        return ArticleDto.from(articleRepository.save(article));
    }

    private Set<Hashtag> renewHashtagsFromContent(ArticleDto articleDto) {
        if (articleDto.hastagDtos() == null) return Set.of();

        return articleDto.hastagDtos().stream()
                .map(HashtagDto::hashtagName) // 1. 이름을 꺼낸다
                .map(name -> hashtagRepository.findByHashtagName(name) // 2. DB에서 찾는다 (Optional<Hashtag> 반환)
                        .orElseGet(() -> hashtagRepository.save(Hashtag.of(name))) // 3. 없으면 저장하고 그 객체를 반환
                )
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        return ArticleDto.from(article);
    }

    // 파라미터에서 받은 articleId, requestBody에서 받은 articleDto
    public ArticleDto updateArticle(Long articleId, ArticleDto articleDto) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        if(article.getUserAccount().getUserId().equals(articleDto.userAccountDto().userId())) {
            if(articleDto.title() != null) article.setTitle(articleDto.title());
            if(articleDto.content() != null) article.setContent(articleDto.content());

            article.clearHashtags();
            articleRepository.flush();          // DELETE 쿼리 즉시 날아감.

            Set<Hashtag> hashtags = articleDto
                    .hastagDtos().stream()
                    .map(hashtagDto -> findOrCreateHashtag(hashtagDto.hashtagName()))
                    .collect(Collectors.toUnmodifiableSet());       // 비영속 객체

            article.addHashtags(hashtags);

        }
        return ArticleDto.from(article);
    }

    public Hashtag findOrCreateHashtag(String hashtagName) {
        return hashtagRepository.findByHashtagName(hashtagName)
                .orElseGet(() -> hashtagRepository.save(Hashtag.of(hashtagName)));
    }

    public void deleteArticle(Long articleId, String userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);
        if(article.getUserAccount().getUserId().equals(userId)) {
            articleRepository.delete(article);
        }
    }
}

// @Transactional: 메서드 실행 도중 Runtime error 발생하면 그동안의 작업 취소, db를 이전상태로 돌림(Rollback)
// readOnly = true: JPA는 데이터를 조회하면 "이 데이터가 나중에 바뀔지도 몰라" 하고 복사본(스냅샷)을 만들어 둠.
// 그리고 트랜잭션이 끝날 때 원본과 비교해서 바뀐 게 있으면 DB에 반영(더티 체킹).
// JPA는 스냅샷을 만들지 않고 변경 감지도 하지 않음. 메모리가 절약되고 속도가 빨라짐.

// getReferenceById는 "일단 ID만 적힌 영수증(Proxy)을 줄게. 진짜 물건(Data)은 나중에 필요할 때
// 이 영수증 보여주면(Method Call) DB에서 가져다줄게"라고 약속하는 방식.
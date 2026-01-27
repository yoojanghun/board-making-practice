package com.example.demo.repository;

import com.example.demo.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {            // 검색 조건 커스터마이징

    Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Article> findByContentContainingIgnoreCase(String content, Pageable pageable);

    // Article 내부의 userAccount 객체로 가서 그 안의 userId 필드 참조
    Page<Article> findByUserAccount_UserIdContainingIgnoreCase(String userId, Pageable pageable);

    Page<Article> findByUserAccount_NicknameContainingIgnoreCase(String nickname, Pageable pageable);

    @Query(
            value = "SELECT DISTINCT a FROM Article a " +
                    "JOIN a.articleHashtags ah " +
                    "JOIN ah.hashtag h " +
                    "WHERE h.hashtagName IN :names",
            countQuery = "SELECT COUNT(DISTINCT a) FROM Article a " +
                    "JOIN a.articleHashtags ah " +
                    "JOIN ah.hashtag h " +
                    "WHERE h.hashtagName IN :names"
    )
    Page<Article> findByHashtagNames(@Param("names") List<String> names, Pageable pageable);
}

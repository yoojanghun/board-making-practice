package com.example.demo.config;

import com.example.demo.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)                        // 스레드에 저장된 보안 정보(Context)
                .filter(Authentication::isAuthenticated)                        // 인증된 상태인지 확인
                .map(Authentication::getPrincipal)                              // 로그인한 사용자의 구체적인 정보(Principal)
                .filter(BoardPrincipal.class::isInstance)                       // 우리가 정의한 BoardPrincipal인지 확인
                .map(BoardPrincipal.class::cast)                                // 캐스팅
                .map(BoardPrincipal::getUsername)                               // 사용자의 ID(예: "user123")를 반환
                .or(() -> Optional.of("system"));                          // 로그인을 안 한 상태라면 관리자 이름인 "system"을 기본값으로 사용
    }
}

// 현재 로그인한 사용자가 누구인지 찾아주는 파일
// 댓글이나 게시글 저장할 때 @CreatedBy, @LastModifiedBy 필드에 로그인한 사용자의 id를 자동으로 채워줌

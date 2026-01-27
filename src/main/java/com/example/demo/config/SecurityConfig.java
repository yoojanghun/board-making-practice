package com.example.demo.config;

import com.example.demo.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration                  // IOC container가 이 클래스를 Bean 설정으로 인식
@EnableWebSecurity              // Spring Security 지원 활성화
public class SecurityConfig {

    // front Controller에 request가 도달하기 전에 Spring Security Filter를 먼저 거침
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)      // CSRF 토큰 안 줌. 검사도 안 함. (JWT 사용 시 보통 disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/sign-up"
                        ).permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .logout(LogoutConfigurer::permitAll)        // 로그아웃 URL엔 누구나 접근 허용
                .build();
    }

    // 비밀번호를 DB에 평문(Plain Text)으로 저장하지 않고, 암호화(해싱)해서 저장
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

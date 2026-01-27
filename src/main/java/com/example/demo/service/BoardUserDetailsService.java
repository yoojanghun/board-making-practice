package com.example.demo.service;

import com.example.demo.dto.security.BoardPrincipal;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService는 사용자가 로그인할 때 Spring Security가 자동으로 호출하는 클래스
// 자동으로 loadUserByUsername 호출
@Service
public class BoardUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public BoardUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findById(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음: " + username));
    }
}

// 사용자가 로그인 시도 => loadUserByUsername 실행 => BoardPrincipal 객체 생성
// 이 객체는 Spring Security의 메모리(세션/SecurityContextHolder)에 저장
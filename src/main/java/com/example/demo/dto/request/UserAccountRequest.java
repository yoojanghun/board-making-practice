package com.example.demo.dto.request;

import com.example.demo.dto.UserAccountDto;
import org.springframework.security.crypto.password.PasswordEncoder;

public record UserAccountRequest(
        String userId,
        String userPassword,
        String email,
        String nickname,
        String memo
) {
    public UserAccountDto toDto(PasswordEncoder passwordEncoder) {
        return UserAccountDto.of(
                userId,
                passwordEncoder.encode(userPassword),
                email,
                nickname,
                memo
        );
    }
}

package com.example.project_board.dto;

import com.example.project_board.domain.UserAccount;

import java.time.LocalDateTime;

// record는 DTO(Data Transfer Object)를 만들기 위한 class
// 자동으로 필드선언, getter, 생성자, equals, hashCode 등 생성
public record UserAccountDto(
        Long id,
        String userId,
        String userPassword,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static UserAccountDto of(Long id, String userId, String userPassword, String email, String nickname, String memo, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(id, userId, userPassword, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    // Entity -> DTO
    public static UserAccountDto from(UserAccount entity) {
        return new UserAccountDto(
                entity.getId(),
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getNickName(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    // DTO(자기자신) -> Entity
    public UserAccount toEntity() {
        return UserAccount.of(
                this.userId,
                this.userPassword,
                this.email,
                this.nickname,
                this.memo
        );
    }
}

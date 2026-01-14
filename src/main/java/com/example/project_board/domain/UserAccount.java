package com.example.project_board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "userId", unique = true),
        @Index(columnList = "email", unique = true),    // 같은 이메일 가입 방지
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(length = 50, nullable = false)
    private String userId;                              // 사용자 아이디

    @Setter
    @Column(nullable = false)
    private String userPassword;                        // 사용자 비밀번호

    @Setter
    @Column(length = 100)
    private String email;

    @Setter
    @Column(length = 100)
    private String nickName;

    @Setter
    private String memo;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(length = 100, nullable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy;

    protected UserAccount() {}

    private UserAccount(String userId, String userPassword, String email, String nickName, String memo) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.email = email;
        this.nickName = nickName;
        this.memo = memo;
    }

    public static UserAccount of(String userId, String userPassword, String email, String nickName, String memo) {
        return new UserAccount(userId, userPassword, email, nickName, memo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

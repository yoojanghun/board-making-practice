package com.example.demo.dto.security;

import com.example.demo.domain.UserAccount;
import com.example.demo.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

// UserDetails: 사용자 한 명을 표현하기 위한 인터페이스.
// DB에서 User 객체를 바로 쓰는 것이 아닌 Spring이 이해 가능한 UserDetails로 감싸서 사용해야 함.
// 기본 UserDetails엔 username, password, authorities정도 밖에 없음
public record BoardPrincipal(
    String username,
    String password,
    Collection<? extends GrantedAuthority> authorities,
    String email,
    String nickname,
    String memo
) implements UserDetails {

    public static BoardPrincipal from(UserAccount entity) {

        Set<RoleType> roleTypes = Set.of(RoleType.USER);
        return new BoardPrincipal(
                entity.getUserId(),
                entity.getUserPassword(),
                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet())
                ,
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo()
        );
    }

    // username -> userId
    public UserAccountDto toDto() {
        return UserAccountDto.of(username,password,email,nickname,memo);
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}
    @Override public String getPassword() {return password;}
    @Override public String getUsername() {return username;}
    @Override public boolean isAccountNonExpired() {return true;}
    @Override public boolean isAccountNonLocked() {return true;}
    @Override public boolean isCredentialsNonExpired() {return true;}
    @Override public boolean isEnabled() {return true;}

    public enum RoleType {
        USER("ROLE_USER");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }
}

package com.example.demo.global.security.userdetails;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.role.Role;
import com.example.demo.domain.user.enums.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


// Spring Security에서 "인증된 사용자 정보"를 담는 객체
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails{

    private final UUID id;
    private final String email;
    private final String password;
    private final Role role;
    private final UserStatus status;

    // DB 인증용
    public CustomUserDetails(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.status = user.getStatus();
    }

    // 토큰 인증용
    public CustomUserDetails(UUID id, String email, Role role){
        this.id = id;
        this.email = email;
        this.password = null; // 토큰 기반 인증 시에는 비밀번호가 필요 없음
        this.role = role;
        this.status = UserStatus.ACTIVE;  // 토큰이 유효하다면 사용자는 활성 상태로 간주
    }

    // 사용자의 권한 목록
    // ROLE_USER, ROLE_ADMIN 같은 값
    // 지금은 권한 설계 안 했으니 빈 리스트, 나중에 Role enum 붙이면 여기 수정
    // TODO: 권한 정책 도입 시 ROLE 기반으로 수정
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // Spring Security가 비밀번호 비교할 때 호출함
    // 로그인 시 입력 비밀번호 vs DB 비밀번호
    // PasswordEncoder로 비교
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 계정 만료 여부
    // false면 로그인 자체가 막힘
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

     // 계정 잠김 여부
     // false면 로그인 실패
     // (ex. 관리자에 의해 정지된 계정)
    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED;
    }

     // 비밀번호 만료 여부
     // false면 로그인 실패
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

     // 계정 활성화 여부
     // false면 로그인 실패
     // (탈퇴, 비활성 계정 등)
    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}

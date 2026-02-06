package com.example.demo.domain.user.entity;

import com.example.demo.domain.user.role.Role;
import com.example.demo.domain.user.role.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickName;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // 탈퇴한 사용자 기간 내 재활성화
    public void reactivate(String password, String name, String nickName, Role role){
        this.password = password;
        this.name = name;
        this.nickName = nickName;
        this.role = role;
        this.status = UserStatus.ACTIVE;
    }

    public void updateProfile(String name, String profileImageUrl){
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}

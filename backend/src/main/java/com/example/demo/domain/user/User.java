package com.example.demo.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Getter
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

    private String profileImageUrl;

    private User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public static User create(String email, String password){
        validate(email,password);

        return new User(email,password);

//        User user = new User();
//        user.email = email;
//        user.password = password;
//        return user;
    }

    public void updateProfile(String name, String profileImageUrl){
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    private static void validate(String email, String password){
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
    }

}

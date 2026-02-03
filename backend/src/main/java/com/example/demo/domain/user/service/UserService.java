package com.example.demo.domain.user.service;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    // 회원가입
    public User signup(String email, String rawPassword){

        // 이메일 중복 체크
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = rawPassword;
        User user = User.create(email,encodedPassword);

        return userRepository.save(user);
    }

    // 프로필 수정
    public void updateProfile(UUID userId, String name, String profileImageUrl){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        user.updateProfile(name, profileImageUrl);
    }

    // 이메일 유저 조회
    @Transactional(readOnly = true)
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}

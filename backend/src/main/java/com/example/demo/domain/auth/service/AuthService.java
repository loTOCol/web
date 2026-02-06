package com.example.demo.domain.auth.service;

import com.example.demo.domain.auth.dto.request.SignUpRequest;
import com.example.demo.domain.auth.exception.ArealyExistsName;
import com.example.demo.domain.auth.exception.ExistEmailSignUpException;
import com.example.demo.domain.auth.exception.InvalidPasswordException;
import com.example.demo.domain.auth.exception.UserNotFoundException;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.user.role.Role;
import com.example.demo.domain.user.role.UserStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void signUp(SignUpRequest request) {

        userRepository.findByEmail(request.email())
                // 1. 이메일에 해당하는 유저가 존재할 경우 (Optional이 비어있지 않을 경우)
                .ifPresentOrElse(
                        user -> {
                            // 1-1. 유저가 존재하지만, 탈퇴 상태(WITHDRAWAL)인 경우
                            if (user.getStatus() == UserStatus.WITHDRAWAL) {
                                reSignUp(user, request); // 재가입 로직 실행
                            } else {
                                // 1-2. 유저가 존재하며, 탈퇴 상태가 아닌 경우 (ACTIVE, BANNED 등)
                                throw new ExistEmailSignUpException();
                            }
                        },
                        // 2. 이메일에 해당하는 유저가 존재하지 않을 경우 (Optional이 비어있을 경우)
                        () -> firstSignUp(request)
                );
    }

    @Transactional
    public void reSignUp(User withdrawnUser , SignUpRequest request) {
        if(isDuplicateName(request.name())) {
            throw new ArealyExistsName(request.name());
        }

        // 기존 User 엔티티의 상태를 업데이트 (새로 생성하는 것이 아님)
        withdrawnUser.reactivate(
                passwordEncoder.encode(request.password()),
                request.name(),
                request.nickName(),
                Role.USER
        );

        // 프로필 이미지 재설정 (없으면 null 유지)
        String profileUrl = (request.profileImageUrl() != null && !request.profileImageUrl().isEmpty())
                ? request.profileImageUrl()
                : null;
        withdrawnUser.updateProfileImage(profileUrl);

    }

    @Transactional
    public void firstSignUp(SignUpRequest request) {
        if(isDuplicateName(request.nickName())) {
            throw new ArealyExistsName(request.nickName());
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .nickName(request.nickName())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .profileImageUrl(request.profileImageUrl())
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean isDuplicateName(String nickName) {
        return userRepository.existsBynickName(nickName);
    }

}

package com.example.demo.global.security.userdetails;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.user.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // DB에서 이메일을 기반으로 사용자 정보를 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 찾을 수 없습니다." + email));

        if(user.getStatus() == UserStatus.BANNED){
            throw new LockedException("벤된 계정입니다.");
        }

        if(user.getStatus() == UserStatus.WITHDRAWAL){
            throw new LockedException("탈퇴한 계정입니다.");
        }

        return new CustomUserDetails(user);
    }
}













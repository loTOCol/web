package com.example.demo.global.jwt.service;

import com.example.demo.domain.auth.dto.response.TokenResponse;
import com.example.demo.domain.auth.exception.UserNotFoundException;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.user.role.Role;
import com.example.demo.global.jwt.JwtProvider;
import com.example.demo.global.jwt.exception.JwtInvalidSignatureException;
import com.example.demo.global.jwt.exception.JwtTokenExpiredException;
import com.example.demo.global.redis.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RedisTokenRepository redisTokenRepository;
    private final UserRepository userRepository;

    // accessToken,refreshToken 두 토큰을 만들어 반환 해줌
    @Transactional
    public TokenResponse generateTokens(UUID id, String email, Role role) {
        String accessToken = jwtProvider.generateAccessToken(id, email, role);
        String refreshToken = jwtProvider.generateRefreshToken(id, email, role);

        redisTokenRepository.saveRefreshToken(email, refreshToken, jwtProvider.getRefreshTokenExpirationInMillis());

        return new TokenResponse(accessToken, refreshToken);
    }

    // 로그아웃 시 혹은 토큰 재발급 시 유효기간이 남은 액세스토큰을 블랙리스트에 담음
    @Transactional
    public void blacklistToken(String token) {
        long expiration = jwtProvider.getTokenRemainingTime(token);
        if (expiration > 0) {
            redisTokenRepository.blacklistToken(token, expiration);
        }
    }

    // 리프레시토큰을 삭제
    @Transactional
    public void deleteRefreshToken(String email) {
        // if문이 없어도, Redis는 키가 존재할 때만 삭제하고 없으면 그냥 지나친다.
        redisTokenRepository.deleteRefreshToken(email);
    }

    // 토큰 재발급
    @Transactional
    public TokenResponse reissueTokens(String refreshToken) {

        if (!jwtProvider.isValid(refreshToken)) {
            throw new JwtTokenExpiredException();
        }
        if(!Objects.equals(jwtProvider.getCategory(refreshToken), "refresh")) {
            throw new JwtTokenExpiredException();
        }
        if(jwtProvider.isExpired(refreshToken)) {
            throw new JwtTokenExpiredException();
        }

        String email = jwtProvider.extractEmail(refreshToken);

        if (!redisTokenRepository.existsRefreshToken(email)) {

            throw new JwtTokenExpiredException();
        }

        redisTokenRepository.getRefreshToken(email)
                .filter(saved -> saved.equals(refreshToken))
                .orElseThrow(JwtTokenExpiredException::new);


        User user = userRepository.findById(jwtProvider.extractId(refreshToken))
                .orElseThrow(UserNotFoundException::new);

        // 기존 refreshToken 레디스에서 삭제 처리
        deleteRefreshToken(email);

        // 새 토큰 발급
        return generateTokens(user.getId(), email, user.getRole());
    }

    // 사용자 밴
    @Transactional
    public void banUser(String accessToken, String email) {
        // 1. 액세스 토큰은 블랙리스트 등록
        blacklistToken(accessToken);

        // 2. 리프레시 토큰 삭제
        redisTokenRepository.deleteRefreshToken(email);

        // 3. 사용자 상태 변경 (선택)
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        user.ban();
    }

    // 액세스 토큰 블랙리스트 여부 확인
    @Transactional
    public boolean isBlacklisted(String token) {
        return redisTokenRepository.isBlacklisted(token);
    }

    // 리프레시토큰을 쿠키에 담아줌
    public ResponseCookie setRefreshTokenToCookie(String refreshToken){
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(jwtProvider.getRefreshTokenExpirationInMillis()))
                .sameSite("None")
                .build();
    }

}

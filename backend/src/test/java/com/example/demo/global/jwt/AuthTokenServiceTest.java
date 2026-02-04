package com.example.demo.global.jwt;


import com.example.demo.domain.auth.service.AuthTokenService;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.util.Ut;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("AuthTokenService 생성")
    void init(){
        assertThat(authTokenService).isNotNull();
    }

    @Test
    @DisplayName("jwt 생성")
    void createToken(){
        // 토큰 만료기간 : 1년
        int expireSeconds = 60 * 60 * 24 * 365;
        // 토큰 시크릿 키
        SecretKey secretKey = Keys.hmacShaKeyFor("abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890".getBytes());

        Map<String,Object> originPayload =  Map.of("name","jhon","age",23);

        String jwtStr = Ut.Jwt.createToken(secretKey, expireSeconds,originPayload);
        // jwt 검증
        assertThat(jwtStr).isNotBlank();

        Jwt<?,?> parsedJwt = Jwts
                .parser()                // JWT 파서 생성
                .verifyWith(secretKey)   // 이 키로 서명 검증
                .build()                 // 파서 완성
                .parse(jwtStr);          // JWT 문자열 파싱 + 검증

        Map<String,Object> parsedPayload = (Map<String,Object>) parsedJwt.getPayload();
        assertThat(parsedPayload).containsAllEntriesOf(originPayload);
    }

    @Test
    @DisplayName("access token 생성")
    void accessToke(){
        User user = userService.signup(
                "test@test.com",
                "1234"
        );

        String accessToken = authTokenService.genAccessToken(user);

        assertThat(accessToken).isNotBlank();

        System.out.println("accessToken = " + accessToken);
    }

}

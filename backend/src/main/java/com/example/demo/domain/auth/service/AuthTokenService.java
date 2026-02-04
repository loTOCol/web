package com.example.demo.domain.auth.service;

import com.example.demo.domain.user.entity.User;
import com.example.demo.global.util.Ut;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    public String genAccessToken(User user) {

        int expireSeconds = 60 * 60 * 24 * 365;

        return Ut.Jwt.createToken(
                Keys.hmacShaKeyFor("abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890".getBytes()),
                expireSeconds,
                Map.of("id",user.getId(),"username",user.getEmail())
        );

    }
}

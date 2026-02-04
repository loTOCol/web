package com.example.demo.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
import java.util.Map;

// 상태(state)를 가지지 않고 기능만 제공
public class Ut {

    // JSON 관련 유틸 모음
    public static class Json {

         // Java 객체 ↔ JSON 문자열 변환 담당
         // 생성 비용이 커서 static으로 1개만 사용
        private static final ObjectMapper objectMapper = new ObjectMapper();

        // 객체를 JSON 문자열로 변환
        public static String toString(Object obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Jwt {
        public static String createToken(Key secretKey, int expireSeconds, Map<String, Object> claims){

            // 토큰 발급 시간
            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            String jwt = Jwts.builder()
                    .claims(claims) // payload(JSON)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey) // 서명
                    .compact(); // 문자열 JWT 생성

            System.out.println("jwt = " + jwt);

            return jwt;
        }
    }
}
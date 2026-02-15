package com.example.demo.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.PrintWriter;
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

        // Java 객체를 JSON으로 변환해서 바로 응답(writer)에 출력
        // 주로 HttpServletResponse.getWriter()와 함께 사용
        public static void write(PrintWriter writer, Object obj) {
            try {
                objectMapper.writeValue(writer, obj);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    public static class Jwt {
//        public static String createToken(
//                String keyString, int expireSeconds, Map<String, Object> claims) {
//
//            // 문자열 비밀키를 HMAC-SHA용 SecretKey로 변환
//            // 키 길이가 부족하면 예외 발생
//            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
//
//            Date issuedAt = new Date();
//            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);
//
//            return Jwts.builder()
//                    .claims(claims)       // payload에 claims 설정
//                    .issuedAt(issuedAt)   // 토큰 발급 시간 설정
//                    .expiration(expiration) // 토큰 만료 시간 설정
//                    .signWith(secretKey)  // 비밀키로 서명
//                    .compact();           // JWT 문자열 생성
//        }
//
//        // JWT가 유효한지 여부만 boolean으로 확인
//        public static boolean isValidToken(String keyString, String token) {
//            try {
//                // 유효한 JWT인지 여부만 확인 (만료, 서명 오류 등 포함)
//                // 예외 발생 시 false 반환 (catch로 잡아서 처리)
//                validateToken(keyString, token);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//
//            return true;
//        }
//
//        public static void validateToken(String keyString, String token) {
//            // 유효하지 않은 JWT인 경우 (예: 만료됨, 서명 오류 등) 예외를 그대로 throw함
//            // 호출하는 쪽에서 try-catch로 직접 예외 처리해야 함
//            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
//            Jwts.parser().verifyWith(secretKey).build().parse(token);
//        }
//
//
//        // JWT payload(claims)만 추출
//        public static Map<String, Object> getPayload(String keyString, String jwtStr) {
//
//            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
//
//            return (Map<String, Object>)
//                    Jwts.parser()
//                            .verifyWith(secretKey) // 서명 검증
//                            .build()
//                            .parse(jwtStr)
//                            .getPayload();
//        }
//    }
}
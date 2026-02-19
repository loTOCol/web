package com.example.demo.global.jwt.exception;

import com.example.demo.global.exception.CustomJwtException;

public class JwtUserInactiveException extends CustomJwtException {
    public JwtUserInactiveException() {
        super("비활성 사용자입니다.", "401");
    }
}

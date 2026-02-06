package com.example.demo.domain.auth.dto.request;

public record SignUpRequest(
        String email,
        String password,
        String name,
        String nickName,
        String profileImageUrl
)
{}

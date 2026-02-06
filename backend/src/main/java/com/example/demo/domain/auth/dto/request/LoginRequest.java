package com.example.demo.domain.auth.dto.request;

public record LoginRequest(
        String email,
        String password
) {}

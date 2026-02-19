package com.example.demo.domain.auth.controller;

import com.example.demo.domain.auth.dto.response.TokenResponse;
import com.example.demo.domain.auth.dto.request.SignUpRequest;
import com.example.demo.domain.auth.service.AuthService;
import com.example.demo.global.jwt.service.TokenService;
import com.example.demo.global.response.RsData;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RsData<?> signUp(@RequestBody @Valid SignUpRequest request) {
        authService.signUp(request);
        return RsData.of("200", "회원가입 성공");
    }

    @PostMapping("/reissue")
    public RsData<?> reissue(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = tokenService.reissueTokens(refreshToken);
        ResponseCookie refreshCookie = tokenService.setRefreshTokenToCookie(tokenResponse.getRefreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken());
        response.addHeader("Access-Control-Expose-Headers", "Authorization, Content-Disposition, Set-Cookie");

        return RsData.of("200", "토큰 재발급 성공");
    }
}

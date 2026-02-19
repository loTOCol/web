package com.example.demo.global.jwt;

import com.example.demo.domain.auth.controller.AuthController;
import com.example.demo.domain.auth.dto.response.TokenResponse;
import com.example.demo.domain.auth.service.AuthService;
import com.example.demo.global.jwt.service.TokenService;
import com.example.demo.global.response.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerReissueTest {

    @Mock
    private AuthService authService;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("재발급 API는 새 토큰을 헤더/쿠키로 내려준다")
    void reissueAddsAuthorizationAndCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        TokenResponse tokenResponse = new TokenResponse("new-access-token", "new-refresh-token");
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", "new-refresh-token")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .build();

        when(tokenService.reissueTokens("old-refresh-token")).thenReturn(tokenResponse);
        when(tokenService.setRefreshTokenToCookie("new-refresh-token")).thenReturn(responseCookie);

        RsData<?> result = authController.reissue("old-refresh-token", response);

        assertThat(result.getCode()).isEqualTo("200");
        assertThat(response.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer new-access-token");
        assertThat(response.getHeader(HttpHeaders.SET_COOKIE)).contains("refreshToken=new-refresh-token");
    }
}

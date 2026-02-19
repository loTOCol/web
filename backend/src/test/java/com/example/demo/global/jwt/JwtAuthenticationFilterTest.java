package com.example.demo.global.jwt;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.enums.UserStatus;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.domain.user.role.Role;
import com.example.demo.global.jwt.service.TokenService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.core.context.SecurityContextHolder.clearContext;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        clearContext();
    }

    @Test
    @DisplayName("블랙리스트 토큰이면 인증 실패 처리한다")
    void rejectBlacklistedToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtProvider,
                authenticationEntryPoint,
                tokenService,
                userRepository
        );

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader("Authorization", "Bearer blacklisted-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.isBlacklisted("blacklisted-token")).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint, times(1)).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("활성 상태가 아닌 사용자의 토큰이면 인증 실패 처리한다")
    void rejectInactiveUserToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtProvider,
                authenticationEntryPoint,
                tokenService,
                userRepository
        );

        UUID userId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader("Authorization", "Bearer access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = mock(User.class);

        when(tokenService.isBlacklisted("access-token")).thenReturn(false);
        when(jwtProvider.getCategory("access-token")).thenReturn("access");
        when(jwtProvider.extractId("access-token")).thenReturn(userId);
        when(jwtProvider.extractEmail("access-token")).thenReturn("user@test.com");
        when(jwtProvider.extractRole("access-token")).thenReturn(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.getStatus()).thenReturn(UserStatus.BANNED);

        filter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint, times(1)).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("유효한 액세스 토큰이면 인증 컨텍스트를 설정하고 체인을 진행한다")
    void authenticateWithValidToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtProvider,
                authenticationEntryPoint,
                tokenService,
                userRepository
        );

        UUID userId = UUID.randomUUID();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader("Authorization", "Bearer valid-access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = mock(User.class);

        when(tokenService.isBlacklisted("valid-access-token")).thenReturn(false);
        when(jwtProvider.getCategory("valid-access-token")).thenReturn("access");
        when(jwtProvider.extractId("valid-access-token")).thenReturn(userId);
        when(jwtProvider.extractEmail("valid-access-token")).thenReturn("user@test.com");
        when(jwtProvider.extractRole("valid-access-token")).thenReturn(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.getStatus()).thenReturn(UserStatus.ACTIVE);

        filter.doFilter(request, response, filterChain);

        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
        verify(filterChain, times(1)).doFilter(any(), any());
        assertThat(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }
}

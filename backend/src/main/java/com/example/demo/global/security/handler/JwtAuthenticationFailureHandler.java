package com.example.demo.global.security.handler;


import com.example.demo.global.util.Ut;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorCode;
        String errorMessage;
        int status;

        //  instanceof를 사용해 예외 종류를 확인하고, 그에 맞는 메시지를 설정
        if (exception instanceof UsernameNotFoundException) {
            errorCode = "404";
            errorMessage = "존재하지 않는 계정입니다.";
            status = HttpServletResponse.SC_NOT_FOUND; // 404
        } else if (exception instanceof BadCredentialsException) {
            errorCode = "400";
            errorMessage = "비밀번호가 일치하지 않습니다.";
            status = HttpServletResponse.SC_BAD_REQUEST; // 400
        } else if (exception instanceof DisabledException) {
            errorCode = "403";
            errorMessage = "탈퇴한 계정입니다.";
            status = HttpServletResponse.SC_FORBIDDEN; // 403
        } else if (exception instanceof LockedException) {
            errorCode = "403";
            errorMessage = "밴된 계정입니다.";
            status = HttpServletResponse.SC_FORBIDDEN; // 403
        } else {
            errorCode = "401";
            errorMessage = "인증에 실패하였습니다. 관리자에게 문의하세요.";
            status = HttpServletResponse.SC_UNAUTHORIZED; // 401
        }

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Map을 사용하여 JSON 응답 본문을 생성
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("code", errorCode);
        errorDetails.put("message", errorMessage);

        // ObjectMapper를 사용해 Map을 JSON 문자열로 변환하고 응답
        Ut.Json.write(response.getWriter(), errorDetails);
    }
}


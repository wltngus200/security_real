package com.green.greengram.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

//권한 없는 사람이 접근시 처리
public class JwtAuthenticationAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException/*인증 거부 오류*/ accessDeniedException)
                        throws IOException/*입출력 오류*/, ServletException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN); //403에러 리턴
    }
}


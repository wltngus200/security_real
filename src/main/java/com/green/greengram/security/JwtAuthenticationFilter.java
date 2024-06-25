package com.green.greengram.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor //생성자를 만드는 것(기본 생성자 X)
@Component //빈등록
public class JwtAuthenticationFilter extends OncePerRequestFilter/*추상 클래스: 여러번 요청이 와도 1번만 호출*/ {
    private final JwtTokenProviderV2 jwtTokenProviderV2;

    //provider에서 @component가 없으면 에러 -> 스프링이 빈등록을 해서 DI해 줄 주소값이 X
    //추상 클래스에는 추상메소드와 일반 메소드 공존 가능. 추상메소드(선언부만 존재)는 구현하라는 강제성이 존재
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProviderV2.resolveToken(request);
        log.info("JwtAuthenticationFilter-Token:{}", token);

        //토큰이 정상적으로 저장되어 있고 만료가 되지 않았다면
        if (token != null && jwtTokenProviderV2.isValidateToken(token)) {
            Authentication auth = jwtTokenProviderV2.getAuthentication(token);
        }
        filterChain.doFilter(request,response);
    }

}





//package com.green.greengram.security.jwt;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor //생성자를 만드는 것(기본 생성자 X)
//@Component //빈등록
//public class JwtAuthenticationFilter extends OncePerRequestFilter /*추상 클래스: 여러번 요청이 와도 1번만 호출*/ {
//    private final JwtTokenProviderV2 jwtTokenProvider;
//
//    //provider에서 @component가 없으면 에러 -> 스프링이 빈등록을 해서 DI해 줄 주소값이 X
//    //추상 클래스에는 추상메소드와 일반 메소드 공존 가능. 추상메소드(선언부만 존재)는 구현하라는 강제성이 존재
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException  {
//        //Header의 authorization키에 저장되어 있는 값을 리턴(있으면 문자열(jwt), 없으면 null)
//        //JWT 값이 있으면 로그인 상태, null이면 비로그인 상태(로그아웃 상태)
//        String token = jwtTokenProvider.resolveToken(request);
//        //img, css, jsm favicon등을 요청할 때 pront가 header에 accessToken을 담지 않았을 때(실수 or 비로그인)
//        log.info("JwtAuthenticationFilter-Token:{}", token);
//
//        //토큰이 정상적으로 저장되어 있고 만료가 되지 않았다면>>로그인 처리
//        if (token != null && jwtTokenProvider.isValidateToken(token)) {
//            //SecurityContextHolder 의 Context에 담기 위한 Authentication 객체 생성
//            //token으로부터 myUser 얻고 MyUserDetail 에 담고 UsernamePasswordAuthenticationToken 에 담아 리턴
//            //UsernamePasswordAuthenticationToken이 Authentication의 자식 클래스
//
//            Authentication/*인증*/ auth = jwtTokenProvider.getAuthentication(token);
//            //getAuthorities 인가처리를 위해 Collection을 return
//            //MyUserDetails의 getAuthorities
//
//            //혹시나 싶어 null check
//            if(auth!=null){//객체 주소값을 담으면 인증되었다고 인식
//                SecurityContextHolder.getContext().setAuthentication(auth);
//                //SecurityContextHolder.getContext()에 Authentication 객체 주소값을 담으면 로그인으로 인식
//            }
//        }
//        filterChain.doFilter(request,response);
//        //다음 필터로 넘긴다.
//
//        // 만약 로그인이 필요한 엔드포인트(url)인데 로그인이 되어있지 않으면,
//        //JwtAuthenticationEntryPoint에 의해서 401에러가 리턴
//
//        // 만약 권한이 필요한 엔드포인트(url)인데 권한이 없으면,
//        //JwtAuthenticationAccessDeniedHandler에 의해서 403에러가 리턴
//
//        //엔트포인트 세팅은 어느 클래스에서? SecurityConfigurationd의 filterChain 메소드에서
//
//
//    }
//
//}
//
//
//
//


package com.green.greengram.security.jwt;

import com.green.greengram.security.jwt.JwtTokenProviderV2;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProviderV2 jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // header의 authorization키에 저장되어 있는 값을 리턴(있으면 문자열(JWT), 없으면 null)
        // JWT값이 있으면 로그인 상태, null이면 비로그인 상태(로그아웃 상태)
        String token = jwtTokenProvider.resolveToken(request);
        //img, css, js, favicon 등을 요청할 때와 FE가 header에 accessToken을 담지 않았을 때(FE의 실수 or 비로그인) accessToken이 없음
        log.info("JwtAuthenticationFilter-Token: {}", token);

        //토큰이 정상적으로 저장되어 있고, 만료가 되지 않았다면 >> 로그인 처리
        if(token != null && jwtTokenProvider.isValidateToken(token)) {
            //SecurityContextHolder의 Context의 담기 위한 Authentication 객체 생성
            //token으로부터 myUser얻고 > MyUserDetails에 담고 > UsernamePasswordAuthenticationToken에 담아서 리턴
            //UsernamePasswordAuthenticationToken이 Authentication의 자식(자손) 클래스
            Authentication auth = jwtTokenProvider.getAuthentication(token);

            if(auth != null) {
                //SecurityContextHolder.getContext()에 Authentication 객체 주소값을 담으면 인증(로그인)되었다고 인식
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
        /*
            다음 필터로 넘긴다.

            만약 로그인이 필요한 엔드포인트(url)인데 로그인이 되어있지 않으면
            JwtAuthenticationEntryPoint에 의해서 401에러를 응답

            만약 권한이 필요한 엔드포인트(url)인데 권한이 없으면
            JwtAuthenticationAccessDeniedHandler에 의해서 403에러를 응답

            엔드포인트 세팅은 SecurityConfiguration의 securityFilterChain메소드에서 한다.
         */
    }
}
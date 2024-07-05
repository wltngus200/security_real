package com.green.greengram.security;

import com.green.greengram.security.jwt.JwtAuthenticationAccessDeniedHandler;
import com.green.greengram.security.jwt.JwtAuthenticationFilter;
import com.green.greengram.security.jwt.JwtAuthenticationEntryPoint;
import com.green.greengram.security.oauth2.MyOAuth2UserService;
import com.green.greengram.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.green.greengram.security.oauth2.OAuth2AuthenticationRequestBasedOnCookieRepository;
import com.green.greengram.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //항상 bean이 붙은 메소드가 존재 함
@RequiredArgsConstructor
public class SecurityConfiguration {
    //@Component로 빈등록을 하였기 때문에 DI가 된다.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2AuthenticationRequestBasedOnCookieRepository repository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final MyOAuth2UserService myOAuth2UserService;
    /*Security와 관련된 빈등록을 여러개 -> 메소드 레벨 빈등록=나중에 취합하기 좋음
        메소드 빈등록으로 하지 않으면 각각 클래스로 만들어야 한다.*/
    @Bean //메소드 타입의 빈등록(파라미터, 리턴타입 중요) 파라미터는 빈등록할 때 필요한 객체
    //어노테이션 메소드가 있으면 스프링 컨테이너가 무조건 호출 파라미터 주소값을 받을 수 있는 애도 가지고있음
    //메소드에만 적용 가능 스프링컨테이너가 무조건 호출 파라미터는 다른 곳에서 빈등록을 하거나 스프링이 줄 수 있는 애로
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity/*라이브러리에 빈등록 되어있음+스프링이 DI*/) throws Exception{
        //이 타입으로 담을 수 있는 객체를 생성(다형성)

        //파라미터 없이 내가 직접 new 객체화 해서 리턴으로 빈등록 가능


        return httpSecurity.sessionManagement(session -> /*람다식*/
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS/*상태 없음*/)) //jwt를 할 것이기 때문에 필요 X
                                                    //세팅: 시큐리티에서 세션 사용하지 않겠다(람다식)
                .httpBasic(http->http.disable())
                //SSR 하지 않는다, html 화면을 백엔드가 만들지 않는다
                //백엔드에서 만들지 않더라도 위 세팅을 끄지 않아도 괜찮다. 사용하지 않는 걸 끔으로써 리소스 확보 위함
                //스프링에서 제공하는 로그인 화면을 사용하지 않겠다. 왜? 안 예뻐서 ㅋㅋㅋㅋㅋㅋㅋㅋ
                .formLogin(form->form.disable()) //form 로그인 방식 사용 X SSL X html 화면을 백엔드가 만들지 않아 필요 X
                //시큐리티에서 폼 로그인 할 때 보안문제로 HTML 폼테그 작성시 뭔가 더 적음
                //form 로그인할 때 내가 보내준 문서(화면)에서 했는지..
                .csrf(csrf->csrf.disable()) //CSRF(cors와 많이 헷갈려함)  //사이트간 요청 위조  끄는 이유: 로그인 방식이 세션일때만 가능(우리는 토큰)
                //cross site Request forgery 이걸 막겠다고 안 해놓은 사이트>> 이 전페이지가 자신의 페이지였는지 확인하는 용
                // A->B로 이체 하는 요청 (요청은 알아내기 쉬움) A사용자에게 링크를 보내면 그 링크를 누르면 요청이 날아가게
                //A가 은행에 들어가 요청을 보내는 게 정상적이지만, 비정상적 요청 get방식> 본인 사이트로 가서 POST방식
                //탈취 개념은 아님 원하는 요청이 원하는 페이지로 날아가도록하는 것 //백에서 할 것
                //.cors(cors->cors.disable())//백엔드에서 막을 수 있는 방법X, 프론트 쪽에서 문제
                //브라우저 자체에서 cors에러를 터트리는 경우 존재(백에서 cors 싫어한다고 되어있어서 막음, 브라우저 설정을 바꾸면 날아감)
                //백에서는 cors가 싫다고만 할 수 있음(브라우저에서 막지만, 브라우저 설정(옵션)을 바꾸면 가능)
                //백엔드가 준 문서에서 요청이 들어오는 것은 안 막겠다, 다른 사이트에서 오는 요청은 싫어!
                //내가 준 HTML문서에서 요청이 들어오면 좋고 아니면 싫음
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                        "/api/feed"
                                        , "/api/feed/*"
                                        , "/api/user/pic"
                                        , "/api/user/follow"
                                )
                                .authenticated()
                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAuthenticationAccessDeniedHandler())
                )
                .oauth2Login( oauth2 -> oauth2.authorizationEndpoint(
                                        auth -> auth.baseUri("/oauth2/authorization")
                                                .authorizationRequestRepository(repository)

                                )
                                .redirectionEndpoint( redirection -> redirection.baseUri("/*/oauth2/code/*"))
                                .userInfoEndpoint(userInfo -> userInfo.userService(myOAuth2UserService))
                                .successHandler(oAuth2AuthenticationSuccessHandler)
                                .failureHandler(oAuth2AuthenticationFailureHandler)
                ).build();


    }

    @Bean
    public PasswordEncoder/*implement 한 것*/ passwordEncoder(){ //암호화 된 패스워드 인코딩 할 때 사용 //스프링에서 지정한 표준(어느 인코더를 사용하더라도 가능 ex. 멀티플러그)
        return new BCryptPasswordEncoder();
    }

}

 /* 체이닝 기법

    만약, permitAll메소드가 void였다면 아래와 같이 작성을 해야함
    .authorizeHttpRequests(auth -> {

        auth.requestMatchers("/api/user/sign-up","/api/user/sign-in").permitAll();
        auth.requestMatchers("/api/ddd").authenticated();
    })
    permitAll 메소드가 자기 자신의 주소값을 리턴한다면 체이닝 기법 가능
    .authorizeHttpRequests(auth -> {
        auth.requestMatchers("/api/user/sign-up","/api/user/sign-in").permitAll()
            .requestMatchers("/api/ddd").authenticated();
    })
                */

/*
기존 주소 차단

        로그인 되어있지 않아도 사용 가능해야함
        "/api/user/sign-up","/api/user/sign-in", "/api/user/access-token",
                스웨거 보이게
                "/swagger","/swagger-ui/**", "/v3/api-docs/**",
                사진 WebMvcConfigurer 참고
                "/pic/**", "/fimg/**",
                프론트 화면 보일 수 있게
                "/", "/index.html"골격,
                "/css/**"화면 색, 위치 등을 관장,"/js/**"이러한 동작을 했을때 이렇게 하자!,"/static/**",프론트 화면 보이게
        //자바스크립트가 없으면 두가지는 정적이 됨
        //프론트가 따로 서버를 돌리면 이 작업을 하지 않아도 됨
        프론트의 라우터 주소
                "/sign-up", "sign-in", "/profile/*"1차까지만 허용, "/feed",
                actuator
                "/actuator", "/actuator/*")
                .permitAll()//로그인 하지 않아도 사용할 수 있다. //체이닝 기법
                        .anyRequest()어떤 요청이든
.authenticated()권한이 있는 자만 사용할 수 있다)//순서가 중요 url을 설정한 부분이 any 뒤에 있으면 적용 X
        //스웨거가 안 뜬다는 부작용 존재 //이미지가 안 뜬다는 부작용(백에서 요청받아서 뜨는 이미지, 프론트랑 상관 X)
        //이젠 화면이 안 뜸 localhost8080 스테틱에 있는 index.html이 실행 안 되기 때문 "/"를 추가, index.html도 추가
        //자바스크립트 파일도 다 막힘
        // api/user/sign-up만 허용했는데 /sign-up 화면이 뜨는 이유(엔터 치면 막힘):SPA특징
        //-> 싱글페이지 어플리케이션 (화면이 하나) 리액트나 뷰는 화면이 전환 되는 것이지 이동이 되는 것 X(라우터)
        //화이트 보드 하나에 뭐 하나 클릭하면 다 지우고 새로 그림(텍스트만 바꿀 뿐 이동이 되지 않음, 엔터를 치는 순간 이동이 됨)
        //local 8080엔터에서 바뀌는 이유는 index.html을 처리-> 첫 화면을 내보내볼까(라우터 처리)
        //라우터처리(화면 전환) 리다이렉션...?
        //우리가 프론트에서 쓰는 라우터 주소를 모두 허용해 주어야 함
        //
        //<-> 바뀌는 부분만 그림 ajax
        .addFilterBefore이 필터 이전에 끼워짐(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling 에러(exception->exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint()401 에러)
        .accessDeniedHandler(new JwtAuthenticationAccessDeniedHandler())403 에러)
        .build();
*/
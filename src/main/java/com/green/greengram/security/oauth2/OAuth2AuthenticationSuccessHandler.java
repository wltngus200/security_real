package com.green.greengram.security.oauth2;

import com.green.greengram.common.CookieUtils;
import com.green.greengram.common.model.AppProperties;
import com.green.greengram.security.MyUser;
import com.green.greengram.security.MyUserDetails;
import com.green.greengram.security.MyUserOAuth2VO;
import com.green.greengram.security.jwt.JwtTokenProviderV2;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthenticationRequestBasedOnCookieRepository repository;
    private final AppProperties appProperties;
    private final CookieUtils cookieUtils;
    private final JwtTokenProviderV2 jwtTokenProvider;

    /*
    <프론트엔드 redirect_uri가 허용한 uri인지 체크하는 부분의 현 상태>
    인증받고 싶은 소셜로그인 선택  (redirect_uri는 소셜 로그인 마무리되고 프론트엔드로 가는 url)
    소셜로그인 화면 출력
    아이디/비번 입력해서 로그인 시도 (redirect_uri는 백엔드로 가는 url) (요청정보 쿠키 저장)
    인가코드 받기 위한 작업이 이루어짐
    제공자(provider)는 아이디/비번이 일치한다면 백엔드 redirect_uri로 인가코드 보내준다.
    백엔드는 인가코드로 access_token을 받기 위한 작업이 이루어짐
    백엔드는 accee_token으로 사용자 정보를 받기 위한 작업이 이루어짐
    로컬 로그인 작업 수행 (SuccessHandler) (프론트엔드 redirect_uri가 허용한 uri인지 체크)
    프론트엔드 redirect_uri로 리다이렉트를 하면서 필요한 정보를 파라미터로 보내준다.
    인증받고 싶은 소셜로그인 선택시 프론트엔드 redirect_uri가 허용한 uri인지 체크를 하도록 변경, 필터로 해결
 */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("targetUrl: {}", targetUrl);
        if (response.isCommitted()) { //응답 객체가 만료된 경우(다른 곳에서 응답한 경우)
            log.error("onAuthenticationSuccess-응답이 만료됨");
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

        @Override
        //FE로 돌아갈 redirect 주소값(즉, FE가 redirect_uri 파라미터로 백엔드에 보내준 값)
        //OAuth2AuthenticationRequestBasedOnCookieRepository의 값을 받아옴
        //응답을 담아보낸 쿠키가 다시 요청으로 돌아온다??
        protected String determineTargetUrl (HttpServletRequest request, HttpServletResponse response, Authentication authentication){
            String redirectUri = cookieUtils.getCookie(request, appProperties.getOauth2().getRedirectUriParamCookieName(), String.class/*타입 지정*/);

            //yaml.app.oauth2.uthorized-redirect-uris 리스트에 없는 Uri인 경우



            //FE가 원하는 redirect_url값이 저장
            String targetUrl = redirectUri == null ? getDefaultTargetUrl() : redirectUri;
            //userId, nm, pic, access_token을 FE에 return
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal()/*object*/;
            MyUserOAuth2VO myUserOAuth2VO = (MyUserOAuth2VO) myUserDetails.getMyUser();
            //myUser Access, Refresh token 만들 때 활용
            MyUser myUser = MyUser.builder()
                    .userId(myUserOAuth2VO.getUserId())
                    .roles(myUserOAuth2VO.getRoles())
                    .build();

            String accessToken = jwtTokenProvider.generateAccessToken(myUser);
            String refreshToken = jwtTokenProvider.generateRefreshToken(myUser);

            //refreshToken은 보안 쿠키를 이용해서 처리
            //refreshToken은 보안 쿠키를 이용해 처리(pront가 따로 작업을 하지 않아도 아래 cookie 값은 항상 넘어온다.)
            //쿠키는 항상 넘어오기 때문에 refreshToken이 필요 없어도 계속 보냄
            int refreshTokenMaxAge = appProperties.getJwt().getRefreshTokenCookieMaxAge();
            cookieUtils.deleteCookie(response, appProperties.getJwt().getRefreshTokenCookieName());
            cookieUtils.setCookie(response, appProperties.getJwt().getRefreshTokenCookieName(),
                                    refreshToken, refreshTokenMaxAge);

            //http://localhost:8080/oauth/redriect?user_id=1&nm=홍길동 ... 쿼리스트링 만들어줌
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("user_id", myUserOAuth2VO.getUserId())
                    .queryParam("nm", myUserOAuth2VO.getNm()).encode()
                    .queryParam("pic", myUserOAuth2VO.getPic())
                    .queryParam("access_token", accessToken)
                    .build().toUriString();

        }

        private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response){
            super.clearAuthenticationAttributes(request);
            repository.removeAuthorizationRequestCookies(response);
        }

        //우리가 설정한 redirect-uri인지 체크 yaml에서 설정한 list값과 매칭 확인

}

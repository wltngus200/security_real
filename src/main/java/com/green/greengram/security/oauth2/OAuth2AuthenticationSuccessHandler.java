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
            if (redirectUri != null && !hasAuthorizedRedirectUri/*yaml 파일에서 조회*/(redirectUri)) {
                throw new IllegalArgumentException("인증되지 않은 Redirect URI입니다.");
            }



            //FE가 원하는 redirect_url값이 저장
            String targetUrl = redirectUri == null ? getDefaultTargetUrl() : redirectUri;
            //userId, nm, pic, access_token을 FE에 return
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal()/*object*/;
            MyUserOAuth2VO myUserOAuth2VO = (MyUserOAuth2VO) myUserDetails.getMyUser();
            //myUser Access, Refresh token 만들 때 활용
            MyUser myUser = MyUser.builder()
                    .userId(myUserOAuth2VO.getUserId())
                    .role(myUserOAuth2VO.getRole()).build();

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
        private boolean hasAuthorizedRedirectUri (String uri){
            //주소값과 관련된 메소드 지원 ex.쿼리스트링 값 빼오기
            URI savedCoolieRedirectUri/*쿠키에 저장됨*/ = URI.create(uri);
            log.info("savedCoolieRedirectUri.getHost():{}", savedCoolieRedirectUri.getHost());
            //http://localhost:8080/ 까지만 나온다
            log.info("savedCoolieRedirectUri.getPort():{}", savedCoolieRedirectUri.getPort());
            log.info("savedCoolieRedirectUri.getPath():{}", savedCoolieRedirectUri.getPath());


            for (String redirectUri/*yaml에 저장*/: appProperties.getOauth2().getAuthorizedRedirectUris()) {
                URI authorizedUri = URI.create(redirectUri);
                if (savedCoolieRedirectUri.getHost().equalsIgnoreCase(authorizedUri.getHost())
                        && /*port num = int*/savedCoolieRedirectUri.getPort() == authorizedUri.getPort()) {
                    return true; //이후에 더 있더라도 같으면 종료
                }
            }
        return false;
    }
}

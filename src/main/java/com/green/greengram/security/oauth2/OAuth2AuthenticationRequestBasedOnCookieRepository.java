package com.green.greengram.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.CookieUtils;
import com.green.greengram.common.model.AppProperties;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

/*
인가/인증 코드가 1회용인 것 처럼  OAuth2AuthorizationRequest 객체도 1회용으로 사용.
    (인가/인증 코드는 요청 보낼때마다 값이 달라진다)
*/
/*
스프링 시큐리티 OAuth처리 때 사용하는 필터가 2개
Oauth2AuthorizationRequestRedirectFilter(AS 가 필터), Oauth2LoginAuthenticationFilter(AS 나 필터)

OAuth2AuthorizationRequest(as A)는 소셜로그인 요청할 때마다 생성되는 객체
1단계 인가코드(인시코드, 인증코드)를 요청할 때 A를 사용
2단계 AccessToken을 요청한 이후에는 A를 사용할 일이 발생하지 않기 때문에 Cookie에서 삭제

세션을 이용해 처리하는 방식은 확장이 불리함>> Cookie로 해결
이전에 세션에서 삭제 처리를 removeAuthorizationRequest 메소드에서 했었던 거 같음

가 필터에서 removeAuthorizationRequest 메소드를 호출해서 리턴 받은 값 활용
authorizationRequestRepository 시큐리티 설정(Configuration)에 보면 repository를 보내고 있고, 그건 우리가 만든 것

* */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationRequestBasedOnCookieRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final CookieUtils cookieUtils;
    private final AppProperties appProperties;
    private final ObjectMapper om;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        log.info("CookieRepository-loadAuthorizationRequest");
        return cookieUtils.getCookie(request,
                appProperties.getOauth2().getAuthorizationRequestCookieName(),
                OAuth2AuthorizationRequest.class);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("CookieRepository - saveAuthorizationRequest");
        if(authorizationRequest==null){
            this.removeAuthorizationRequestCookies(response);
            return;
        }
        cookieUtils.setCookie(response, appProperties.getOauth2().getAuthorizationRequestCookieName(),
                authorizationRequest,
                appProperties.getOauth2().getCookieExpirySeconds());
        String redirectUriAfterLogin = request.getParameter(appProperties.getOauth2().getRedirectUriParamCookieName());
        log.info("redirectUriAfterLogin: {}", redirectUriAfterLogin);
        if(StringUtils.isNotBlank(redirectUriAfterLogin)){
            cookieUtils.setCookie(response/*응답*/, appProperties.getOauth2().getRedirectUriParamCookieName(),
                    redirectUriAfterLogin, appProperties.getOauth2().getCookieExpirySeconds());
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) { // 삭제 하면서 넘겨줌 not void
        log.info("CookieRepository - removeAuthorizationRequest");
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletResponse response){
        log.info("CookieRepository - removeAuthorizationRequestCookies");
        cookieUtils.deleteCookie(response, appProperties.getOauth2().getAuthorizationRequestCookieName());
        cookieUtils.deleteCookie(response, appProperties.getOauth2().getRedirectUriParamCookieName());
    }
}

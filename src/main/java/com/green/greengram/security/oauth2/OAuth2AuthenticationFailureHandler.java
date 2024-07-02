package com.green.greengram.security.oauth2;

import com.green.greengram.common.CookieUtils;
import com.green.greengram.common.model.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final OAuth2AuthenticationRequestBasedOnCookieRepository repository;
    private final CookieUtils cookieUtils;
    private final AppProperties appProperties;

    //@Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response){
        log.info("OAuth2AUthenticationFailureHandler - onAuthenticationFailure");
        //?????????????
    }
}

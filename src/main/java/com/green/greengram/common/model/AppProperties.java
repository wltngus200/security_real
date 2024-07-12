package com.green.greengram.common.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;

@Getter
@ConfigurationProperties(prefix = "app") //yaml파일의 값 //마우스 올려보면 뜨는 ~~스캔 필요
public class AppProperties {
    private final Jwt jwt=new Jwt();
    //jwt는 yaml의 값(대 소문자 구분 X) static을 빼도 됨(이너클래스일때만 static 붙이기 가능)
    private final Oauth2 oauth2=new Oauth2();

    @Getter
    @Setter
    public static class Jwt{ //각 멤버필드 이름은 yaml(카멜케이스)파일 값과 매칭
        //yaml 파일내의 값과 매칭 되고, secret는 보안을 위해 .env(JsonIgnore)
        private String secret;
        private String headerSchemaName;
        private String tokenType;
        private long accessTokenExpiry;
        private long refreshTokenExpiry;
        //yaml에서 저장되지 않음
        private int refreshTokenCookieMaxAge; //JS로 접근 할 수 없는 토큰
        private String refreshTokenCookieName;

        public void setRefreshTokenExpiry(long refreshTokenExpiry){
            this.refreshTokenExpiry=refreshTokenExpiry;
            this.refreshTokenCookieMaxAge=(int)(refreshTokenExpiry*0.001); //ms->s 쿠키는 초값 사용
        }
    }
    @Getter
    @Setter
    public static class Oauth2{
        private String baseUri;
        private String authorizationRequestCookieName;
        private String redirectUriParamCookieName;
        private int cookieExpirySeconds;
        private List<String> authorizedRedirectUris;
    }

}

package com.green.greengram.security.oauth2;

import com.green.greengram.security.SignInProviderType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2UserInfoFactory/*factory=객체 생성*/ {
    //구글, 카카오, 네이버에서 받은 Json Data > HashMap > 규격화 된 객체로 변환
    public OAuth2UserInfo getOAuth2UserInfo(SignInProviderType signInProviderType, Map<String, Object> attributes){
        return switch (signInProviderType){
            case GOOGLE/*같은 패키지 안에 있어서*/ -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            default -> throw new RuntimeException("제공하지 않는 로그인 방식입니다.");
        };
    }
}

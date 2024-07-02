package com.green.greengram.security.oauth2;

import java.util.Map;
//규격화 : 각 사이트마다 데이터 저장하는 방식이 다른 것을 일률적으로 맞춤
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    public KakaoOAuth2UserInfo(Map<String, Object> attribute) {
        super(attribute);
    }

    @Override
    public String getId() {
        return attribute.get("id").toString();//다른 형변환 방법
    }

    @Override
    public String getName() {
        Map<String, Object> properties =(Map<String, Object>)attribute.get("properties");
        return properties==null?null:properties.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("account_email").toString();
    }

    @Override
    public String getProfilePicUrl() {
        Map<String, Object> properties =(Map<String, Object>)attribute.get("properties");
        return properties==null? null:properties.get("thumbnail_image").toString();
    }
}

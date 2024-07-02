package com.green.greengram.security.oauth2;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo{
    public NaverOAuth2UserInfo(Map<String, Object> attribute) {
        super(attribute);
    }

    @Override
    public String getId() {
        Map<String, Object> response=(Map<String, Object>)attribute.get("response");
        return response==null?null:response.get("id").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> response=(Map<String, Object>)attribute.get("response");
        return response==null?null:response.get("name").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> response=(Map<String, Object>)attribute.get("response");
        return response==null?null:response.get("email").toString();
    }

    @Override
    public String getProfilePicUrl() {
        Map<String, Object> response=(Map<String, Object>)attribute.get("response");
        return response==null?null:response.get("profile_image").toString();
    }
}

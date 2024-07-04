package com.green.greengram.security.oauth2;

import java.util.Map;
//규격화 : 각 사이트마다 데이터 저장하는 방식이 다른 것을 일률적으로 맞춤
public class GoogleOAuth2UserInfo extends OAuth2UserInfo{//부모는 자식객체 주소값 가질수 있음
    //롬복의 @RequiredArgConstructor
    //롬복으로 해결 X(내가 가진 final 멤버필드에 대해서만 생성자)
    /*부모가 기본 생성자가 아닌 생성자(오버라이딩 생성자)만 가지고 있는 경우는
          lombok으로 처리 불가 직접 생성자 작성 필요
    */

    public GoogleOAuth2UserInfo(Map<String, Object> attribute) {
        super(attribute); //final attributes에 DI됨
    }

    @Override
    public String getId() {
        return (String)attribute.get("sub");
        //map에서 Object를 return하기 때문에 에러
    }

    @Override
    public String getName() {
        return (String)attribute.get("name");
    }

    @Override
    public String getEmail() {
        return (String)attribute.get("email");
    }

    @Override
    public String getProfilePicUrl() {
        return (String)attribute.get("picture");
    }
}

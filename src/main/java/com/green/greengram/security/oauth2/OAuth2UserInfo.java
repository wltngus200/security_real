package com.green.greengram.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor //DI를 스프링 컨테이너가 하는 것 x 내가 함
public abstract class OAuth2UserInfo {
    //map put get으로 넣고 꺼냄
    protected/*상속관계도 접근 가능*/ final Map<String/*key*/, Object/*value*/> attribute;
    // social 플랫폼에서 받아온 DATA(json)dmf HashMap으로 컨버팅하여 내가 DI

    public abstract String getId(); //유니크 값 리턴
    public abstract String getName(); //이름
    public abstract String getEmail(); //이메일
    public abstract String getProfilePicUrl();//프로필 사진 http:// or null

    //해당 페이지에서 제공해주지 않으면 따로 모달창을 띄워 입력 받아야 함

}

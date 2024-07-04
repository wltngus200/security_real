package com.green.greengram.security;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

//SELECT를 했는데 Success쪽에서도 또 SELECT를 하기 때문에, 이미 조회된 정보를 저장
//MyOAuth2UserService의 User user = mapper.getUserId(signInParam);
//프론트에게 최초 로그인시 넘겨주는 정보 signInPostRes=(userId, nm, pic)
//my User는 PK값만 담을 수 있기 때문에, nm과 pic을 찾기 위해 또 SELECT가 필요
@Getter
public class MyUserOAuth2VO/*setter가 없다*/ extends MyUser{
    private final String nm;
    private final String pic;
    /*수정 불가(이뮤터블)*/

    public MyUserOAuth2VO(long userId, String role/*부모가 필요*/, String nm, String pic/*내가 필요*/) {
        super(userId, role);
        this.nm = nm;
        this.pic = pic;
    }
}

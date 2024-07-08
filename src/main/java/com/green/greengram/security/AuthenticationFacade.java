package com.green.greengram.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

//로그인 한 사용자 정보를 가져오는 객체를 만듦
// (Security Context Holder > Context > Authentication(로그인한 사용자 정보 저장-(MyUserDetails))의 getPrincipal 정보를 얻어와서)
                                        //JwtAuthenticationFilter의 UsernamePasswordAuthenticationToken
//Authentication 해당 위치에 저장이 되어 있어야만 스프링 시큐리티가 로그인으로 여긴다.
//로그인 정보를 어디서 넣었는가

@Component //빈등록
public class AuthenticationFacade {
    public MyUser getLoginUser(){
        MyUserDetails/*객체 주소값이 들어가는 시점=JwtAuthenticationFilter*/ myUserDetails
                =(MyUserDetails)SecurityContextHolder/*시큐리티가 만드는 객체*/
                .getContext()
                .getAuthentication()
                .getPrincipal/*우리가 넣었던 정보를 빼와서 처리한다*/();
        return myUserDetails==null? null:myUserDetails.getMyUser();
    }
    //이뮤터블 =setter가 없다 한번 값을 넣고 바뀌지 않았으면 한다
    public long getLoginUserId(){
        MyUser myUser=getLoginUser();
        return myUser==null?0: myUser.getUserId();
    }
}

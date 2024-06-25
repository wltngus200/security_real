package com.green.greengram.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CookieUtils {//백에서 쿠키는 작업할 수 있지만 헤더는 불가능
    public Cookie getCookie(HttpServletRequest req, String name){
        Cookie[] cookies=req.getCookies();
        if(cookies!=null && cookies.length>0){
            for(Cookie cookie : cookies){ // 쿠키도 key - value로 이루어짐
                if(name.equals(cookie.getName())){ //찾는 key가 있는지 확인
                    return cookie;//같은 이름을 찾았다면 물어볼 필요 x
                }
            }
        }
        return null;
    }

    public void setCookie(HttpServletResponse res, String name, String value, int maxAge){
        Cookie cookie =new Cookie(name, value);
        cookie.setPath("/"); //root URL 우리 백엔드 모든 요청에 해당하게 세팅
        cookie.setHttpOnly(true); //보안쿠키
        cookie.setMaxAge(maxAge);
        res.addCookie(cookie);
    }

    public void deleteCookie(HttpServletResponse res, String name){
        setCookie(res, name, null, 0);
    }
}

package com.green.greengram.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.springframework.web.util.WebUtils.getCookie;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtils {//백에서 쿠키는 작업할 수 있지만 헤더는 불가능
    private final ObjectMapper om;

    public <T> T getData(T type, HttpServletRequest req, String name){
        try{
            Cookie cookie=getCookie(req, name);
            String json=cookie.getValue();
            return (T)om.readValue(json, type.getClass());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Cookie getCookie(HttpServletRequest req, String name){ //쿠키를 받기 위함
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

    public <T> T getCookie(HttpServletRequest req, String name, Class<T> valueType){ //객체를 얻기 위함
        Cookie cookie =getCookie(req, name);
        try{
            return om.readValue(cookie.getValue(), valueType);
        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

//    public void setCookie(HttpServletResponse res, String name, String value, int maxAge){
//        Cookie cookie =new Cookie(name, value);
//        cookie.setPath("/"); //root URL 우리 백엔드 모든 요청에 해당하게 세팅
//        cookie.setHttpOnly(true); //보안쿠키
//        cookie.setMaxAge(maxAge);
//        res.addCookie(cookie);
//    }

    public void setCookie(HttpServletResponse res, String name, Object value, int maxAge){
        try{
            this.setCookie(res,name, om.writeValueAsString(value), maxAge);
        }catch(JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteCookie(HttpServletResponse res, String name){
        setCookie(res, name, null, 0);
    }
    //제네릭 컴파일 시점에서 데이터의 타입이 정해짐

}

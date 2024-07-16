package com.green.greengram.security;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@NoArgsConstructor
@Setter
@Getter
public class MyUserDetails implements UserDetails, OAuth2User {
    private MyUser myUser;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //단수 > 복수로 변경
        //1)return Collections.singletonList/*1개 가진 리스트 반환*/(new SimpleGrantedAuthority(myUser.getRole()));
        //2)List<GrantedAuthority> list2=new ArrayList();
        //  list2.add(new SimpleGrantedAuthority("ROLE_USER"));
        //위와 아래는 동일

        //List<String> >> List<GrantedAuthority> 변경하는 작업(상속관계이기에 들어갈 수 있다)
        //부모 타입은 자식객체 주소값 담을 수 있다
        List<GrantedAuthority> list=new ArrayList();
        for(String role : myUser.getRoles()){
            list.add(new SimpleGrantedAuthority(role));
        }
        return list;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return myUser ==null? "GUEST":String.valueOf(myUser.getUserId());
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }


    @Override
    public String getName() {
        return null;
    }
}

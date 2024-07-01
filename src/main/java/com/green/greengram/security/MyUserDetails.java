package com.green.greengram.security;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class MyUserDetails implements UserDetails {
    private MyUser myUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //List<GrantedAuthority> list = new ArrayList();
        //list.add(new SimpleGrantedAuthority(role));
        //return list;
        return Collections.singletonList(new SimpleGrantedAuthority(myUser.getRole()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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


}

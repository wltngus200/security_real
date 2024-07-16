package com.green.greengram.user.model;

import lombok.Getter;
import lombok.Setter;

//@AllArgsConstructor는 현재 내가 적은 멤버필드만 생성자를 제공
@Getter
@Setter
public class UserInfo extends User {
    private String role;

    //생성자로 값을 넣을 때는 값의 순서와 SELECT순서가 같아야 함

}

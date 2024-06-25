package com.green.greengram.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignInPostReq {
    //@Schema(example="mic", description = "유저 아이디")
    private String uid;
    //@Schema(example="1212", description = "유저 비밀번호")
    private String upw;

    private String accessToken;
}

package com.green.greengram.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.green.greengram.security.SignInProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignUpPostReq {
    @JsonIgnore
    private long userId;

    @JsonIgnore
    private SignInProviderType providerType;
    //String일 경우 service에 .name이 필요

    @Schema(example="mic", description = "유저 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uid;
    @Schema(example="1212", description = "유저 비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String upw;
    @Schema(example="홍길동", description = "유저 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nm;


    @JsonIgnore
    private String pic; //파일의 위치값을 담음
}

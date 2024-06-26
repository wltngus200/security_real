package com.green.greengram.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter
@Setter
public class UserInfoGetReq {
    //@Schema(name="signed_user_id", defaultValue="1", description = "로그인 한 유저 프로필")
    @JsonIgnore
    private long signedUserId;//로그인 개념 들어가면 안 씀
    @Schema(name="profile_user_id", defaultValue="2", description = "다른 유저 프로필")
    private long profileUserId;
    //쿼리스트링으로 넘어감 대문자 안 쓰고 싶은데 2가지니까 생성자로 처리
    //1개씩 바꾼다= 바인드 파람/ 여러개 바꾼다 constructorProperties



    @ConstructorProperties({"signed_user_id", "profile_user_id"})
    public UserInfoGetReq(long signedUserId, long profileUserId){
        this.signedUserId=signedUserId;
        this.profileUserId=profileUserId;
    }

}

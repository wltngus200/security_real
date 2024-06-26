package com.green.greengram.userfollow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.BindParam;

import java.beans.ConstructorProperties;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class UserFollowReq {
    //@BindParam("from_user_id")
    //@Schema(example="17", description="팔로우 유저 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonIgnore
    private long fromUserId;

    @JsonProperty("to_user_id") //Json을 보낼 때 이렇게 보내야 함
    @Schema(example="17", description="팔로잉 유저 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
    private long toUserId;

    @ConstructorProperties({ "to_user_id" })
    public UserFollowReq(long toUserId) {
        //this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }
}

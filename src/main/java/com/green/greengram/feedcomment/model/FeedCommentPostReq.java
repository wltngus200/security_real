package com.green.greengram.feedcomment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedCommentPostReq {
    @JsonIgnore
    private long feedCommentId;
    //INSERT를 할 때 PK값이 생성 -> 그 PK 값을 담을 수 있는 멤버필드
    //<insert id="insFeedComment" keyProperty="feedCommentId" useGeneratedKeys="true">
    //인터페이스와 xml이 매칭 되어 DAO메소드 생성 //그 때에 사용할 파라미터이면서 PK값을 저장하는 역할

    private long feedId;
    private long userId;
    private String comment;
}

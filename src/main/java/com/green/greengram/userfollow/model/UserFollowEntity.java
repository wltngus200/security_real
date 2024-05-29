package com.green.greengram.userfollow.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class UserFollowEntity {
    //테이블과 완전히 매칭되었을 때 Entity
    private long fromUserId;
    private long toUserId;
    private String createdAt;
}

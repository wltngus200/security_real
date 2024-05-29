package com.green.greengram.feedfavorite.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedFavoriteToggleReq {
    private long feedId;
    private long userId;
    //어떤 피드에 누가 언제 좋아요를 했다는 정보(복합 PK-insert, delete에 사용)

}

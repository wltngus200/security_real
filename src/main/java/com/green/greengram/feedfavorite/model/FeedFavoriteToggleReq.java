package com.green.greengram.feedfavorite.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.websocket.server.PathParam;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.PathVariable;

import java.beans.ConstructorProperties;

@Getter
//@Setter
@ToString
@EqualsAndHashCode
public class FeedFavoriteToggleReq {
    private long feedId;
    private long userId;

    @ConstructorProperties({"feed_id", "user_id"})
    public FeedFavoriteToggleReq(long feedId, long userId){
        this.feedId=feedId;
        this.userId=userId;
    }
    //어떤 피드에 누가 언제 좋아요를 했다는 정보(복합 PK-insert, delete에 사용)

}

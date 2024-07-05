package com.green.greengram.feedfavorite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.websocket.server.PathParam;
import lombok.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.beans.ConstructorProperties;

@Getter
//@Setter
@ToString
@EqualsAndHashCode
public class FeedFavoriteToggleReq {
    @Schema(name = "feed_id")
    private long feedId;

    @JsonIgnore
    private long userId;

    @ConstructorProperties({"feed_id"})
    public FeedFavoriteToggleReq(long feedId){
        this.feedId=feedId;
    }
    //어떤 피드에 누가 언제 좋아요를 했다는 정보(복합 PK-insert, delete에 사용)

    public void setUserId(long userId) {
        this.userId = userId;
    }
}

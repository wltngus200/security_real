package com.green.greengram.feedfavorite.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedFavoriteEntity {
    private long feedId;
    private long userId;
    private String createdAt;

}

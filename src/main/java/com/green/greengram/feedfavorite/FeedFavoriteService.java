package com.green.greengram.feedfavorite;

import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import lombok.EqualsAndHashCode;


public interface FeedFavoriteService {
    int toggleFavorite(FeedFavoriteToggleReq p);
}

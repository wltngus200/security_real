package com.green.greengram.feedfavorite;

import com.green.greengram.common.model.MyResponse;
import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;

public interface FeedFavoriteController {
    MyResponse<Integer> toggleFavorite(FeedFavoriteToggleReq p);
}

package com.green.greengram.feedfavorite;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface FeedFavoriteController {
    ResultDto<Integer> toggleFavorite(FeedFavoriteToggleReq p);
}

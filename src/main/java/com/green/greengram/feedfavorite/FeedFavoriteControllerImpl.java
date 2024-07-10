package com.green.greengram.feedfavorite;

import com.green.greengram.common.model.MyResponse;
import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/feed/favorite")
@RequiredArgsConstructor
public class FeedFavoriteControllerImpl implements FeedFavoriteController{
    private final FeedFavoriteService service;

    @Override
    @GetMapping
    public MyResponse<Integer> toggleFavorite(@ModelAttribute @ParameterObject FeedFavoriteToggleReq p){
        int result=service.toggleFavorite(p);
        return MyResponse.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg(result == 0? "좋아요 취소" : "좋아요")
                .resultData(result)
                .build();
    }
}

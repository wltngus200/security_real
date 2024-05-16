package com.green.greengram.feedfavorite;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/feed/favorite")
@RequiredArgsConstructor
public class FeedFavoriteController {
    private final FeedFavoriteService service;

    @PostMapping
    public ResultDto<Integer> postLike(@RequestBody FeedFavoriteToggleReq p){
        int result=service.postLike(p);
        return ResultDto.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg((result==0?"좋아요 취소":"좋아요 처리")+"ο(=•ω＜=)ρ⌒☆ 좋아요 처음 해봐요")
                .resultData(result)
                .build();
    }
}

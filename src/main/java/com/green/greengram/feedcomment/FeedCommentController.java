package com.green.greengram.feedcomment;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/feed/comment")
public class FeedCommentController {
    private final FeedCommentService service;

    @PostMapping
    public ResultDto<Long> insFeedComment(@RequestBody FeedCommentPostReq p){
        long result=service.insFeedComment(p);
        return ResultDto.<Long>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("악플 ᕦ(ò_óˇ)ᕤ 안돼!")
                .resultData(result)
                .build();
    }

    @DeleteMapping
    public ResultDto<Integer> deleteFeedComment(@PathVariable @ModelAttribute FeedCommentDeleteReq p){
        //@RequestBody(Json, 파일)=Post Put에 적절(노출 되어서는 안 되는 데이터)
        //@PathVariable(적은 데이터)=Get Delete에 적절
        int result=service.deleteFeedComment(p);
        return ResultDto.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("ψ(._. )> 가는 거야?")
                .resultData(result)
                .build();
    }

}


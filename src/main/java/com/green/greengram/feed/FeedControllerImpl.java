package com.green.greengram.feed;

import com.green.greengram.common.model.MyResponse;
import com.green.greengram.feed.model.FeedGetReq;
import com.green.greengram.feed.model.FeedGetRes;
import com.green.greengram.feed.model.FeedPostReq;
import com.green.greengram.feed.model.FeedPostRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("api/feed")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FeedControllerImpl {
    private final FeedService service;

    @PostMapping
    public MyResponse<FeedPostRes> postFeed(@RequestPart List<MultipartFile> pics, @RequestPart FeedPostReq p){
        FeedPostRes result=service.postFeed(pics, p);
        return MyResponse.<FeedPostRes>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ 올렸엉")
                .resultData(result)
                .build();
    }

    @GetMapping
    public MyResponse<List<FeedGetRes>> getFeed(@ModelAttribute @ParameterObject FeedGetReq p){
        List<FeedGetRes> list=service.getFeed(p);
        return MyResponse.<List<FeedGetRes>>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("(╬▔皿▔)╯ 아, 왜 안 되는데")
                .resultData(list)
                .build();
    }

    @DeleteMapping
    public MyResponse<Integer> deleteFeed(@ModelAttribute @ParameterObject long feedId){
        int result=service.deleteFeed(feedId);
        return MyResponse.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultData(result)
                .resultMsg("╰(艹皿艹 ) 으...")
                .build();
    }
}

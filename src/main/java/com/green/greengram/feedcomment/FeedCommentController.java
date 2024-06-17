package com.green.greengram.feedcomment;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface FeedCommentController {
    ResultDto<Long> insFeedComment(@RequestBody FeedCommentPostReq p);
    ResultDto<Integer> deleteFeedComment(@PathVariable @ModelAttribute FeedCommentDeleteReq p);
    ResultDto<List<FeedCommentGetRes>> feedCommentListGet(long feedId);
}

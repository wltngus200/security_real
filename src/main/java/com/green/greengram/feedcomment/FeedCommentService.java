package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;

import java.util.List;

public interface FeedCommentService {

    long insFeedComment(FeedCommentPostReq p);
    int deleteFeedComment(FeedCommentDeleteReq p);
    List<FeedCommentGetRes> feedCommentListGet(long feedId);
}

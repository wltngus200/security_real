package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;

public interface FeedCommentService {

    long insFeedComment(FeedCommentPostReq p);
    int deleteFeedComment(FeedCommentDeleteReq p);
}

package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedCommentMapper {
    int insFeedComment(FeedCommentPostReq p);
    int deleteFeedComment(FeedCommentDeleteReq p);
}

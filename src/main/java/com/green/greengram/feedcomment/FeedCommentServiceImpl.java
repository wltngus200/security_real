package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCommentServiceImpl implements FeedCommentService{
    private final FeedCommentMapper mapper;

    public long insFeedComment(FeedCommentPostReq p){
        int affectedRows= mapper.insFeedComment(p);
        return p.getFeedCommentId();
    }

    public int deleteFeedComment(FeedCommentDeleteReq p){
        return mapper.deleteFeedComment(p);
    }
}

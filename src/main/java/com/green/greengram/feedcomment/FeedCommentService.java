package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCommentService {
    private final FeedCommentMapper mapper;

    long insFeedComment(FeedCommentPostReq p){
        int affectedRows= mapper.insFeedComment(p);
        return p.getFeedCommentId();
    }

    int deleteFeedComment(FeedCommentDeleteReq p){
        return mapper.deleteFeedComment(p);
    }
}

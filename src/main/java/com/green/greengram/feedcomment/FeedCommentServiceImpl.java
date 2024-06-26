package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import com.green.greengram.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedCommentServiceImpl implements FeedCommentService{
    private final FeedCommentMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public long insFeedComment(FeedCommentPostReq p){
        p.setUserId(authenticationFacade.getLoginUserId());
        int affectedRows= mapper.insFeedComment(p);
        return p.getFeedCommentId();
    }

    public int deleteFeedComment(FeedCommentDeleteReq p){
        return mapper.deleteFeedComment(p);
    }

    public List<FeedCommentGetRes> feedCommentListGet(long feedId) {
        return mapper.feedCommentList(feedId);
    }
}

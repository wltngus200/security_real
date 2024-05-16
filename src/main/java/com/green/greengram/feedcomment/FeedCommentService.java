package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCommentService {
    private final FeedCommentMapper mapper;

    int postComment(FeedCommentPostReq p){
        return mapper.postComment(p);
    }

    int deleteComment(FeedCommentDeleteReq p){
        return mapper.deleteComment(p);
    }
}

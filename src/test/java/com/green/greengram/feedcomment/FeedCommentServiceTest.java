package com.green.greengram.feedcomment;

import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({FeedCommentServiceImpl.class})
@ExtendWith(SpringExtension.class)
class FeedCommentServiceTest {

    @MockBean
    private FeedCommentMapper mapper;

    @Autowired
    private FeedCommentService service;
    @Test
    void insFeedComment() {
        FeedCommentPostReq req1=new FeedCommentPostReq();
        req1.setFeedCommentId(1);
        FeedCommentPostReq req2=new FeedCommentPostReq();
        req2.setFeedCommentId(5); //나머진 의미 없는 멤버필드

        assertEquals(req1.getFeedCommentId(), service.insFeedComment(req1));
        assertEquals(req2.getFeedCommentId(), service.insFeedComment(req2));

        verify(mapper).insFeedComment(req1);
        verify(mapper).insFeedComment(req2);
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteFeedComment() {
        FeedCommentDeleteReq req1=new FeedCommentDeleteReq(10,20);
        FeedCommentDeleteReq req2=new FeedCommentDeleteReq(100, 200);

        given(mapper.deleteFeedComment(req1)).willReturn(1);
        given(mapper.deleteFeedComment(req2)).willReturn(0);

        assertEquals(1, service.deleteFeedComment(req1),"1의 리턴값이 다름");
        assertEquals(0, service.deleteFeedComment(req2),"2의 리턴 값이 다름");

        verify(mapper, times(1)).deleteFeedComment(req1);
        verify(mapper, times(1)).deleteFeedComment(req2);
    }

    @Test
    void feedCommentListGet() {
        long feedId1=1;
        long feedId2=2;
        FeedCommentGetRes res1= new FeedCommentGetRes();
//        res1.setComment();
//        res1.setFeedCommentId();
//        res1.setCreatedAt();
//        res1.setWriterPic();
//        res1.setWriterNm();
//        res1.setWriterId();
//
        FeedCommentGetRes res2= new FeedCommentGetRes();

        List<FeedCommentGetRes> list1=mapper.feedCommentList(feedId1);
        List<FeedCommentGetRes> list2=mapper.feedCommentList(feedId2);

        given(mapper.feedCommentList(feedId1)).willReturn(list1);
        given(mapper.feedCommentList(feedId2)).willReturn(list2);

        assertEquals(list1, service.feedCommentListGet(feedId1));
        assertEquals(list2, service.feedCommentListGet(feedId2));

        verify(mapper).feedCommentList(feedId1);
        verify(mapper).feedCommentList(feedId2);
    }
}
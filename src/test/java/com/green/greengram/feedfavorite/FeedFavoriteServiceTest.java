package com.green.greengram.feedfavorite;

import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({FeedFavoriteServiceImpl.class})//테스트를 처음 할 때는 없기 때문에 비워두고 나중에 추가
class FeedFavoriteServiceTest {
    @MockBean
    private FeedFavoriteMapper mapper;

    @Autowired
    private FeedFavoriteService service;

    @Test
    void toggleFavorite() {
        FeedFavoriteToggleReq req1=new FeedFavoriteToggleReq(); //0
        req1.setFeedId(1); req1.setUserId(2);
        FeedFavoriteToggleReq req2=new FeedFavoriteToggleReq(); //1
        req2.setFeedId(10); req2.setUserId(20);
        FeedFavoriteToggleReq req3=new FeedFavoriteToggleReq();
        req3.setFeedId(1); req3.setUserId(4);

        given(mapper.delFeedFavorite(req1)).willReturn(0);
        given(mapper.delFeedFavorite(req2)).willReturn(1);
        //구분을 위한 수치
        given(mapper.insFeedFavorite(req1)).willReturn(100);
        given(mapper.insFeedFavorite(req2)).willReturn(200);
        //given(mapper.delFeedFavorite(req3)).willReturn(0);

        //실제 테스트를 하는 코드
        assertEquals(100, service.toggleFavorite(req1), "1번");
        //del(req1)이 false가 되면서 ins 실행
        assertEquals(0, service.toggleFavorite(req2), "2번");
        //del(req2)가 true가 되면서 0 리턴
        //assertEquals(1, service.toggleFavorite(req2), "2번");
        //assertEquals(1, service.toggleFavorite(req3), "3번");

        //실제 메소드 호출이 되어 실행이 되었는지
        verify(mapper).delFeedFavorite(req1);
        verify(mapper).delFeedFavorite(req2);

        verify(mapper).insFeedFavorite(req1);
        verify(mapper, never()).insFeedFavorite(req2);

        verify(mapper, times(1)).delFeedFavorite(req1);
        verify(mapper, times(1)).delFeedFavorite(req2);
        //never는 실행이 되면 안 된다
        //적어도 1번 적어도 n번수행 등등 Mockito.verify
        //verify(mapper).delFeedFavorite(req3);
    }
}
package com.green.greengram.feedfavorite;

import com.green.greengram.feedfavorite.model.FeedFavoriteEntity;

import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("tdd")
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class FeedFavoriteMapperTest {
    @Autowired
    FeedFavoriteMapper mapper;

    @Test
    void insFeedFavorite() {
        FeedFavoriteToggleReq all=new FeedFavoriteToggleReq(0, 0);
        List<FeedFavoriteEntity> list1=mapper.selFeedFavoriteForTest(all);
        assertEquals(12, list1.size());

        FeedFavoriteToggleReq p1=new FeedFavoriteToggleReq(10,20);
        int affect=mapper.insFeedFavorite(p1);
        assertEquals(1, affect);//영향 받은 행이 1
        List<FeedFavoriteEntity> list2=mapper.selFeedFavoriteForTest(all);
        assertEquals(1, list2.size()-list1.size()); //추가된 행이 있다
        List<FeedFavoriteEntity> list3=mapper.selFeedFavoriteForTest(p1);
        assertEquals(1,list3.size());



    }

    @Test
    void delFeedFavorite() {
    }

    @Test
    void selFeedFavoriteForTest() {
    }
}
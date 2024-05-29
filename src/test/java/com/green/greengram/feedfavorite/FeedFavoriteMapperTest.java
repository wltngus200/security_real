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
        FeedFavoriteToggleReq all=new FeedFavoriteToggleReq();
        List<FeedFavoriteEntity> list1=mapper.selFeedFavoriteForTest(all);
        assertEquals(10, list1.size(), "전체 리스트");


        FeedFavoriteEntity p0=new FeedFavoriteEntity();
        p0.setFeedId(5);
        p0.setUserId(1);
        p0.setCreatedAt("2024-05-16 19:55:24");
        //createdAt은 관리용->우선은 수동체크
        assertEquals(p0, list1.get(0),"0번 레코드의 값과 일치하지 않음");
        //Equals비교이기 때문에 해시코드(주소값 기준으로 만듦)가 다르면(별개의 객체면) 다르다고 출력

        FeedFavoriteEntity p3=new FeedFavoriteEntity(); //p(방 번호)
        p3.setFeedId(8);
        p3.setUserId(4);
        p3.setCreatedAt("2024-05-20 12:37:37");

        //중복된 값도 넣어놓고, 없는 값 보냈을 때, feedId만 보냈을 때 userId만 넣었을 때
        //(0개 1개 여러개 넘어오는 경우 모두 확인) 이상한 값 보냈을 때 안 나오는 게 맞는지 등등 다양하게 테스트





    }

    @Test
    void delFeedFavorite() {
    }

    @Test
    void selFeedFavoriteForTest() {
    }
}
package com.green.greengram.feedfavorite;

import com.green.greengram.BaseIntegrationTest;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FeedFavoriteIntegrationTest extends BaseIntegrationTest {//지워지는 거 생기는 거 2개 테스트
    private String BASE_URL="/api/feed/favorite";

    @Test
    @Rollback(value = false)
    @DisplayName("좋아요 처리")
    public void insFeedfavofrite() throws Exception{ //get방식
        MultiValueMap<String, String/*오브젝트 안 됨*/> params=new LinkedMultiValueMap();
        params.add("feed_id", "20");
        params.add("user_id", "1");

        MvcResult mr=mvc.perform(get(BASE_URL).params(params)
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String resJson=mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resJson, ResultDto.class);
        assertEquals(1, result.getResultData());
    }

    @Test
    @Rollback(value=false)
    @DisplayName("좋아요 취소")
    public void delFeedfavorite() throws Exception{
        MultiValueMap<String, String/*오브젝트 안 됨*/> params=new LinkedMultiValueMap();
        params.add("feed_id", "20");
        params.add("user_id", "1");

        MvcResult mr=mvc.perform(get(BASE_URL).params(params)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String resJson=mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resJson, ResultDto.class);
        assertEquals(1, result.getResultData());

    }
}

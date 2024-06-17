package com.green.greengram.userfollow;

import com.green.greengram.BaseIntegrationTest;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.userfollow.model.UserFollowReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFollowIntegrationTest extends BaseIntegrationTest {
    private String BASE_URL="/api/user/follow";
    @Test
    @Rollback(false) //실제로 데이터베이스까지 가는지 눈으로 확인
    @DisplayName("post - 유저팔로우")
    public void postUserFollow() throws Exception{ //4번이 1번을 팔로우
        UserFollowReq p=new UserFollowReq(4, 1);
        String reqJson=om.writeValueAsString(p);
        MvcResult mr=mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(reqJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn(); //얘가 없으면 ResultActions를 리턴함

        String resContents/*BODY에 담긴 JSON형태의 문자열*/ = mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resContents/*String(JSON)*/, ResultDto.class/*담으려는 객체*/); //역직렬화
        assertEquals(1, result.getResultData()); //객체에 담긴 값 불러오기

    }

    @Test
    @Rollback(false)
    @DisplayName("delete - 유저팔로우")
    public void deleteUserFollow() throws Exception{
        UserFollowReq p=new UserFollowReq(3,5); //전체 테스트를 진행했을 때 순서를 보장하지 않기 때문에 기존값 할 것

        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        params.add("from_user_id", String.valueOf(p.getFromUserId()));
        params.add("to_user_id", String.valueOf(p.getToUserId()));

        MvcResult mr= mvc.perform(delete(BASE_URL).params(params))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String resContents= mr.getResponse().getContentAsString();
        ResultDto<Integer> result= om.readValue(resContents, ResultDto.class);
        assertEquals(1, result.getResultData());
        //얘만 실행하면 4,1 은 없다 >>왜...?
    }

}

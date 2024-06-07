package com.green.greengram.feedfavorite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.CharEncodingConfiguration;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@Import(CharEncodingConfiguration.class)
@WebMvcTest(FeedFavoriteControllerImpl.class)
class FeedFavoriteControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private FeedFavoriteController controller;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private FeedFavoriteService service;

    @Test
    void toggleFavorite() throws Exception{
        int resultData=1;

        FeedFavoriteToggleReq p=new FeedFavoriteToggleReq();
        p.setFeedId(1);
        p.setUserId(1);
        given(service.toggleFavorite(p)).willReturn(resultData);

        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        params.add("feed_id", String.valueOf(p.getFeedId()));
        params.add("user_id", String.valueOf(p.getUserId()));

        ResultDto<Integer> notYetJson=ResultDto.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg(HttpStatus.OK.toString())
                .resultData(resultData)
                .build();

        String resultJson=om.writeValueAsString(notYetJson);

        mvc.perform(get("/api/feed/favorite").params(params))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson))
                .andDo(print());

        verify(service.toggleFavorite(p));
    }
}
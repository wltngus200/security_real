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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@Import(CharEncodingConfiguration.class) //문자열 중 한글이 깨지지 않게 하기 위함
@WebMvcTest(FeedFavoriteControllerImpl.class) //MOCKMvc를 객체 등록 주입
class FeedFavoriteControllerTest {
    @Autowired
    private ObjectMapper om; //JSON을 객체로 객체를 JSON으로 직렬화(객체에 든 데이터를 표현)하는 라이브러리
    //직렬화가 왜 필요한가 -> 데이터를 전달하기 위해서 // 컴퓨터 객체안의 데이터들의 주소값(8byte)을 레퍼런스 변수에 저장
    //주소값 8인 이유 OS가 64비트라서  1바이트=8비트  //주소값을 전달하면 다른 세상에서는 의미가 없다
    //왜 JSON인가 -> 자바스크립트 오브젝트 노테이션? 표현기법으로 사용 //무슨 데이터가 들어있었는지 표시
    //JSON을 받는 사람이 다시 객체로 바꾸는 이유 -> 쓰기 편하기 때문 문자열에서 내가 원하는 데이터 뽑아쓰기 어려움
    //가상의 프론트를 만들어 받을 수 있는지 테스트를 해 보자 //오브젝트 형태를 json으로 바꾸는 직렬화 역직렬화에 사용
    //주소값을 보내 봤자 다른 플랫폼이면 의미가 없다 각자의 메모리에 있는 주소값으로 접근 했을 때의 데이터 값이 다름

    @Autowired
    private MockMvc mvc; //통신과 결과까지 체크 가능

    @Autowired
    private FeedFavoriteController controller;

    @MockBean
    private FeedFavoriteService service; //빈을 가짜로 만들어 줌
    //컨트롤러가 객체화 할 때, service의 주소값을 생성자를 통해 받아야 하는데 거기에 담을 주소값이 존재하지 않기 때문
    //spring은 null을 주면서 객체화 하지 않는다
    //슬라이스테스트(일정 부분만 테스트)이기 때문에 mockbean을 준다 -> 실제로 import 해 버리면 테스트 규모가 커짐
    //TDD는 완벽하지 않다 모든 것을 테스트 할 수 있지 않다
    //given 테스트를 위한 세팅
    //when
    //then

    @Test
    void toggleFavorite() throws Exception{
        int resultData=1;
        //given
        FeedFavoriteToggleReq p=new FeedFavoriteToggleReq(1,2);
        //쿼리스트링으로 보내는데 대문자 들어감 _id했을 때 들어가게 FeedFavoriteToggleReq 세팅
        given(service.toggleFavorite(p)).willReturn(resultData);
        // JSON으로 바꿈
        String json=om.writeValueAsString(p); //에러를 던지고 있다 //겟 방식이 때문에 JSON아님

        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        //두 가지의 클래스 이름이 다르다= 다형성: 부모가 자식 객체의 주소값 담음
        params.add("feed_id", String.valueOf(p.getFeedId()));
        params.add("user_id", String.valueOf(p.getUserId()));

        Map<String, Object/*모두 타입이 다름*/> result = new HashMap();
        result.put("statusCode", HttpStatus.OK);
        result.put("resultMsg", HttpStatus.OK.toString());
        result.put("resultData", resultData);

        String expectedResJson=om.writeValueAsString(result);

        //when 통신을 함
        mvc.perform(get("/api/feed/favorite").params(params))
                //then
                .andExpect(status().isOk()) //통신이 원활하게 되었으면 200
                //.andExpect(content().json(expectedResJson)) //리턴하는 데이터의 JSON 비교(키=value의 순서가 바뀌어도 상관X)
                .andDo(print()); //콘솔에 찍어라

        verify(service).toggleFavorite(p);
    }


    @Test
    void toggleFavorite2() throws Exception{
        int resultData=1;
        //given
        FeedFavoriteToggleReq p=new FeedFavoriteToggleReq(1,2);
        //쿼리스트링으로 보내는데 대문자 들어감 _id했을 때 들어가게 FeedFavoriteToggleReq 세팅
        given(service.toggleFavorite(p)).willReturn(resultData);
        // JSON으로 바꿈
        String json=om.writeValueAsString(p); //에러를 던지고 있다 //겟 방식이 때문에 JSON아님

        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        //두 가지의 클래스 이름이 다르다= 다형성: 부모가 자식 객체의 주소값 담음
        params.add("feed_id", String.valueOf(p.getFeedId()));
        params.add("user_id", String.valueOf(p.getUserId()));

        Map<String, Object/*모두 타입이 다름*/> result = new HashMap();
        result.put("statusCode", HttpStatus.OK);
        result.put("resultMsg", HttpStatus.OK.toString());
        result.put("resultData", resultData);

        String expectedResJson=om.writeValueAsString(result);

        //when 통신을 함
        mvc.perform(get("/api/feed/favorite").params(params))
                //then
                .andExpect(status().isOk()) //통신이 원활하게 되었으면 200
                //.andExpect(content().json(expectedResJson)) //리턴하는 데이터의 JSON 비교(키=value의 순서가 바뀌어도 상관X)
                .andDo(print()); //콘솔에 찍어라

        verify(service).toggleFavorite(p);
    }
}
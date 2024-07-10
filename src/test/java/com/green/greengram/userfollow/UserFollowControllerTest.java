package com.green.greengram.userfollow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.CharEncodingConfiguration;
import com.green.greengram.common.model.MyResponse;
import com.green.greengram.userfollow.model.UserFollowReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CharEncodingConfiguration.class) //한글이 깨지지 않게 하는 도구
@WebMvcTest(UserFollowControllerImpl.class)
class UserFollowControllerTest {
    @Autowired
    private ObjectMapper om;//jack son이라고 하는 Json 라이브러리

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserFollowController controller;

    @MockBean
    private UserFollowServiceImpl service;
        //JSON을 보내고 Json을 받아서 검증하는 것이 핵심
    @Test
    void postUserFollow() throws Exception { //예외가 터지면 실패이기 때문에 우리도 던져버림
        UserFollowReq p=new UserFollowReq(1, 2);
        int resultData=1;
        given(service.postUserFollow(p)).willReturn(resultData);
        String json=om.writeValueAsString(p); //예외 처리 throw하고 있다 //입력을 JSon화

        //리턴을 기대하는 값
         MyResponse<Integer> expectedResult= MyResponse.<Integer>builder()
                                            .statusCode(HttpStatus.OK)
                                            .resultMsg(HttpStatus.OK.toString())
                                            .resultData(resultData) //1
                                            .build();
//         Map expectedResultMap=new HashMap();
//         expectedResultMap.put("statusCode",HttpStatus.OK);
//         expectedResultMap.put("resutlMsg",HttpStatus.OK.toString());
//         expectedResultMap.put("resultData", resultData);
         //자바에서 JSON으로 만들떄 자주 사용 하는 것이 오브젝트와 MAP

        String expectedResultJson=om.writeValueAsString(expectedResult);//응답을 Json화

        mvc.perform(MockMvcRequestBuilders//통신을 담당 //예외처리 throw하고 있다
                .post("/api/user/follow") // /를 자동으로 추가 안 해주는 듯?
                .contentType(MediaType.APPLICATION_JSON) //RequestBody로 받음
                .content(json) //JsonType으로 자동으로 변경 X
        )//Post 해 본다
                .andExpect(status().isOk()) //200 코드가 날아왔는지 확인
                .andExpect(content()/*JSON*/.json(expectedResultJson))//*실제 넘어온 데이터(ResultDto)가 String으로 내가 원하는 값인지*/)
                //실제 값을 적기엔 어려움(이스케이프 문자 등 들어감)
                .andDo/*perform이 끝나고 이거 하자*/(print()/*콘솔에다가 찍어라*/);

        verify(service).postUserFollow(p);
    }

    @Test
    void postUserFollow2() throws Exception { //예외가 터지면 실패이기 때문에 우리도 던져버림
        //given?
        UserFollowReq p=new UserFollowReq(1, 2);
        int resultData=10;
        given(service.postUserFollow(p)).willReturn(resultData);
        String json=om.writeValueAsString(p); //예외 처리 throw하고 있다

        //리턴을 기대하는 값
        MyResponse<Integer> expectedResult= MyResponse.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg(HttpStatus.OK.toString())
                .resultData(resultData) //1
                .build();
        Map expectedResultMap=new HashMap();
        expectedResultMap.put("statusCode",HttpStatus.OK);
        expectedResultMap.put("resutlMsg",HttpStatus.OK.toString());
        expectedResultMap.put("resultData", resultData);
        //자바에서 JSON으로 만들떄 자주 사용 하는 것이 오브젝트와 MAP

        String expectedResultJson=om.writeValueAsString(expectedResult);

        //when
        mvc.perform(MockMvcRequestBuilders//통신을 담당 //예외처리 throw하고 있다
                //static메소드이기 때문에 MockMvcRequestBuilders 생략 가능 아래 Delete참고
                        .post("/api/user/follow")
                        .contentType(MediaType.APPLICATION_JSON) //RequestBody로 받음
                        .content(json) //JsonType으로 자동으로 변경 X //BODY에 실림
                )//then
                .andExpect(status().isOk()) //200 코드가 날아왔는지 확인
                .andExpect(content()/*JSON*/.json(expectedResultJson))//*실제 넘어온 데이터(ResultDto)가 String으로 내가 원하는 값인지*/)
                //실제 값을 적기엔 어려움(이스케이프 문자 등 들어감)
                .andDo(print()); //perform이 끝나고 이거 하자 콘솔에다가 찍어라

        verify(service).postUserFollow(p);
    }

    @Test
    void deleteUserFollow() throws Exception{
        UserFollowReq p=new UserFollowReq(1, 1);
        int resultData=1;
        //호출해서 리턴한 값이 ResultDto에 resultDto에 들어가는가
        given(service.deleteUserFollow(p)).willReturn(resultData);
        //key String value String
        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        params.add("from_user_id", String.valueOf(p.getFromUserId()));
        params.add("to_user_id", String.valueOf(p.getToUserId()));

        MyResponse<Integer> expectedResult= MyResponse.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg(HttpStatus.OK.toString())
                .resultData(resultData) //1
                .build();

        String expectedResultJson=om.writeValueAsString(expectedResult);

        //수행
        mvc.perform(delete("/api/user/follow")//반복적으로 쓰여서 멤버필드로 하길 추천
                .params(params) //쿼리스트링
                        //"/api/user/follow?from_user_id=1&to_user_id=2 //위의 숫자를 바꿀 때 마다 바꿔주어야 함
        ).andExpect(status().isOk())
        .andExpect(content().json(expectedResultJson))
        .andDo(print());
        verify(service).deleteUserFollow(p);
    }
}
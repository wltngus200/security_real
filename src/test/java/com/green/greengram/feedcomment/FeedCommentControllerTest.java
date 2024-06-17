package com.green.greengram.feedcomment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.CharEncodingConfiguration;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import com.green.greengram.feedfavorite.FeedFavoriteControllerImpl;
import com.green.greengram.feedfavorite.FeedFavoriteService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import({CharEncodingConfiguration.class})
@WebMvcTest({FeedCommentControllerImpl.class})
class FeedCommentControllerTest { //컨트롤러를 테스트 하려면 객체화가 되어야한다 //빈등록을 해 놓음(등록을 하고 주소값을 계속 들고 있어라 +내가 요청하면 그 값을 줘라)
    @Autowired private ObjectMapper om; //JSON 직렬화 역직렬화
    @Autowired private MockMvc mvc; //
    @MockBean private FeedCommentService service; //컨트롤러가 객체화 하기 위해서 필요함
    //DI 의존성 주입 : 외부에서 주소값이 들어오는 행위를 DI(그 행위를 스프링컨테이너가 해 줌)
    // 외부에서 준다는 것은 즉 외부에서 객체 생성되었다는 의미
    // 스프링 컨테이너에 빈등록을 한다(빈등록: 스프링 컨테이너에 객체 생성을 위임)
    // 빈등록을 받는 이유: 나중에 DI를 받기 위해 맞나?????
    // 코드가 IoC로 되어있기 때문
    @Test
    void insFeedComment() throws Exception {
        long resultData=1;
        FeedCommentPostReq p=new FeedCommentPostReq();
        p.setFeedId(1); p.setUserId(2); p.setComment("안녕");
        String reqJson=om.writeValueAsString(p);

        given(service.insFeedComment(p)).willReturn(resultData);

        Map<String, Object> expectedRes= new HashMap();//VO개념 : 사용 방법만 다를 뿐 용도는 같음
        //뭐랑 뭐가.....? 에잉..... 객체는 틀이 있고 얘는 마음 대로 추가 가능
        expectedRes.put("statusCode", HttpStatus.OK);
        expectedRes.put("resultMsg", "악플 ᕦ(ò_óˇ)ᕤ 안돼!");
        expectedRes.put("resultData", resultData);

        //제네릭은 레퍼런스 타입으로 줘야함
        //프라모티브 타입 래퍼 타입 = 값만 담는 용도 VS 주소값을 줘서 메소드 등 활용 가능

        String expectedResJson=om.writeValueAsString(expectedRes);
        //왜 JSON으로 바꾸는가? 제대로 보냈는지 통신 하기 위해

        mvc.perform(post("/api/feed/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJson)
        ).andExpect(status().isOk())
         .andExpect(content().json(expectedResJson))
         .andDo(print());


        verify(service).insFeedComment(p);

    }

    @Test
    void insFeedComment2() throws Exception {
        long resultData=5;//항상 같은 값을 리턴하는 게 아닌지 다시 확인
        FeedCommentPostReq p=new FeedCommentPostReq();
        p.setFeedId(1); p.setUserId(2); p.setComment("안녕");
        String reqJson=om.writeValueAsString(p);

        given(service.insFeedComment(p)).willReturn(resultData);

        Map<String, Object> expectedRes= new HashMap();//VO개념 : 사용 방법만 다를 뿐 용도는 같음
        //뭐랑 뭐가.....? 에잉..... 객체는 틀이 있고 얘는 마음 대로 추가 가능
        expectedRes.put("statusCode", HttpStatus.OK);
        expectedRes.put("resultMsg", "악플 ᕦ(ò_óˇ)ᕤ 안돼!");
        expectedRes.put("resultData", resultData);

        //제네릭은 레퍼런스 타입으로 줘야함
        //프라모티브 타입 래퍼 타입 = 값만 담는 용도 VS 주소값을 줘서 메소드 등 활용 가능

        String expectedResJson=om.writeValueAsString(expectedRes);
        //왜 JSON으로 바꾸는가? 제대로 보냈는지 통신 하기 위해

        mvc.perform(post("/api/feed/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson)
                ).andExpect(status().isOk())
                .andExpect(content().json(expectedResJson))
                .andDo(print());

        verify(service).insFeedComment(p);

    }

    @Test
    void deleteFeedComment() throws Exception {
        int result=1;
        FeedCommentDeleteReq req1=new FeedCommentDeleteReq(1, 2);
        //String json=om.writeValueAsString(req1); 쿼리스트링으로 날리기 때문에 필요없다

        //쿼리스트링을 짠다
        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        params.add("feed_comment_id", String.valueOf(req1.getFeedCommentId()));
        params.add("signed_user_id", String.valueOf(req1.getSignedUserId()));

        //객체를 넣었을 때 리턴할 값 지정
        given(service.deleteFeedComment(req1)).willReturn(result);

        //Dto 꾸미기
        Map<String, Object> map=new HashMap();
        map.put("statusCode", HttpStatus.OK);
        map.put("resultMsg", "ψ(._. )> 가는 거야?");
        map.put("resultData", result);
        String resultJson=om.writeValueAsString(map);

        //통신 시도
        mvc.perform(delete("/api/feed/comment")
                .params(params)
        )//.andExpect(status().isOk())
        .andExpect(content().json(resultJson))
        .andDo(print());//콘솔에 통신 내용을 찍기 위함

        verify(service).deleteFeedComment(req1);
    }

    @Test
    void feedCommentListGet() throws Exception {
        long feedId=1;
        FeedCommentGetRes res1=new FeedCommentGetRes();
        res1.setFeedCommentId(2); res1.setComment("안녕");
        FeedCommentGetRes res2=new FeedCommentGetRes();
        res2.setFeedCommentId(3); res2.setComment("반가워");
        FeedCommentGetRes res3=new FeedCommentGetRes();
        res3.setFeedCommentId(4); res3.setComment("룰룽");
        List commentList=new ArrayList();
        commentList.add(res1);
        commentList.add(res2);
        commentList.add(res3);

        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        params.add("feed_id", String.valueOf(feedId));

        Map<String, Object> expectedRes= new HashMap();
        expectedRes.put("statusCode", HttpStatus.OK);
        expectedRes.put("resultMsg", String.format("rows: %,d", commentList.size()));
        expectedRes.put("resultData", expectedRes);

        String expectedResJson=om.writeValueAsString(expectedRes);

        mvc.perform(get("/api/feed/comment")
                .queryParams(params)
        ).andExpect(status().isOk())
        .andExpect(content().json(expectedResJson))
        .andDo(print());

        verify(service).feedCommentListGet(feedId);
    }
    @Test
    void feedCommentListGet2() throws Exception {
        long feedId=1;
        FeedCommentGetRes res1=new FeedCommentGetRes();
        res1.setFeedCommentId(2); res1.setComment("안녕");
        FeedCommentGetRes res2=new FeedCommentGetRes();
        res2.setFeedCommentId(2); res2.setComment("반가워");
        FeedCommentGetRes res3=new FeedCommentGetRes();
        res3.setFeedCommentId(2); res3.setComment("룰룽");
        List commentList=new ArrayList();
        commentList.add(res1);
        commentList.add(res2);
        commentList.add(res3);

        MultiValueMap<String, String> params=new LinkedMultiValueMap();
        params.add("feed_id", String.valueOf(feedId));

        Map<String, Object> expectedRes= new HashMap();
        expectedRes.put("statusCode", HttpStatus.OK);
        expectedRes.put("resultMsg", String.format("rows: %,d", commentList.size()));
        expectedRes.put("resultData", commentList);

        String expectedResJson=om.writeValueAsString(expectedRes);

        mvc.perform(get("/api/feed/comment")
                        .queryParams(params)
                ).andExpect(status().isOk())
                .andExpect(content().json(expectedResJson))
                .andDo(print());

        verify(service).feedCommentListGet(feedId);
        }
}
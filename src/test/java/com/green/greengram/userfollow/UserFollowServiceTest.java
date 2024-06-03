package com.green.greengram.userfollow;

import com.green.greengram.userfollow.model.UserFollowReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class) //Spring 컨테이너를 쓰고 싶음 (mapper에서는 MybatisTest)
@Import({UserFollowServiceImpl.class}) //임포트 하고싶은 것들을 적음 //정식 테스트 할 때
    //슬라이스 테스트이기 때문에 빠르게 해야됨 얘가 가진 빈 들은 객체화 X mapper가 final이 붙어있지만 넣어줄 값이 X
class UserFollowServiceTest {
    @MockBean
    private UserFollowMapper mapper; //내용 없는 가짜
    //기븐이라는 메소드로 임무를 부여

    @Autowired
    private UserFollowService service;
    //given - when - then(세팅 실행 검증 단계)
    @Test
    void postUserFollow() {
        //given
        UserFollowReq p1=new UserFollowReq(1, 2);
        UserFollowReq p2=new UserFollowReq(1, 3);
        UserFollowReq p3=new UserFollowReq(1, 4);

        given(mapper.insUserFollow(p1)).willReturn(0);
        given(mapper.insUserFollow(p2)).willReturn(1);
        given(mapper.insUserFollow(p3)).willReturn(2);
        //given(mapper.insUserFollow(any())).willReturn(3);//any외에 정확한 값도 가능

        //when
        assertEquals(0, service.postUserFollow(p1));
        assertEquals(1, service.postUserFollow(p2));
        assertEquals(2, service.postUserFollow(p3));


        verify(mapper).insUserFollow(p1);
        verify(mapper).insUserFollow(p2);
        verify(mapper).insUserFollow(p3);


        //verify(mapper).insUserFollow(any());
        // 메소드가 정말로 호출 되었는지 확인 //메소드가 값을 리턴했는지는 알 수 X
    }

    @Test
    void deleteUserFollow() {
    }
}
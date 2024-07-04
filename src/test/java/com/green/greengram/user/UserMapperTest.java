package com.green.greengram.user;

import com.green.greengram.user.model.*;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("tdd")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {
    @Autowired
    private UserMapper mapper;
    @Test
    void getUserId() {
        SignInPostReq req=new SignInPostReq();
        User user1=mapper.getUserId(req); //시큐리티 넣고 나니 에러 터짐
        List<User> userList=mapper.selTest(user1.getUserId());
        User user1Comp=userList.get(0);
        assertEquals(user1Comp, user1, "첫번째 문제");

        User user3=mapper.getUserId(req);
        List<User> userList3=mapper.selTest(user3.getUserId());
        User user3Comp=userList3.get(0);
        assertEquals(user3Comp, user3, "뭐지");

        User userNo=mapper.getUserId(req);
        assertNull(userNo, "없는 사용자 레코드 넘어옴");
    }

    @Test
    void selProfileUserInfo() { //프로필을 봤을 때 유저의 정보
        UserInfoGetReq reqNull=new UserInfoGetReq(0, 0);
        UserInfoGetRes resNull=mapper.selProfileUserInfo(reqNull);
        assertNull(resNull, "없어야되는데 넘어옴");

        UserInfoGetReq req1=new UserInfoGetReq(2, 1);
        UserInfoGetRes res1=mapper.selProfileUserInfo(req1);

        UserInfoGetRes expectedRes1=new UserInfoGetRes();
        expectedRes1.setNm("정형우");
        expectedRes1.setPic("64656731-92ee-48b9-b5cd-010fcdb7f643.jpg");
        expectedRes1.setCreatedAt("2024-05-16 14:39:07");
        expectedRes1.setFeedCnt(3);
        expectedRes1.setFavCnt(0);
        expectedRes1.setFollowing(4);
        expectedRes1.setFollower(2);
        expectedRes1.setFollowState(3);

        //다른 값으로 2~3개 추가적으로 검수
    }

    @Test
    void updProfilePicYou() {

        String picName1="test.gif";
        //제대로 값이 업데이트 되는지
        UserProfilePatchReq req=new UserProfilePatchReq();
        req.setSignedUserId(1);
        req.setPicName(picName1);
        int affectedRows=mapper.updProfilePic(req);
        assertEquals(1, affectedRows);

        List<User> userList1=mapper.selTest(1);
        User user1=userList1.get(0);

        assertEquals(picName1, user1.getPic());

        //없는 유저를 업데이트 했을 때 영향받은 행 =0
        List<User> beforeUserList=mapper.selTest(0);
        UserProfilePatchReq req1=new UserProfilePatchReq();
        req1.setSignedUserId(100);
        req1.setPicName(picName1);
        int affectedRows1=mapper.updProfilePic(req1);

        assertEquals(0, affectedRows1);

        List<User> afterUserList=mapper.selTest(0);
        for(int i=0; i<beforeUserList.size();i++){
            assertEquals(beforeUserList.get(i),afterUserList.get(i));



            // 다른 사용자의 값을 바꾼게 아닌지 확인
            // 수정되기 전 리스트 -> 수정 -> 영향받은 행 1인지 확인
            //-> 수정 된 후 전체 리스트 -> 나머지 변화 없는지 체크
        }

    }


}
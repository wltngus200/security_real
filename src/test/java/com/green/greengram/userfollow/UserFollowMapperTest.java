package com.green.greengram.userfollow;

import com.green.greengram.userfollow.model.UserFollowEntity;
import com.green.greengram.userfollow.model.UserFollowReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*; //import를 static으로 함
@ActiveProfiles("tdd")
@MybatisTest //생성자를 통해 DI받아오기 X
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)//롤백 하지 않겠다를 통해 DB에 반영됨
class UserFollowMapperTest {

    @Autowired //생성자 대신 DI를 받아옴
    private UserFollowMapper mapper;

    @Test
    @DisplayName("유저 팔로우 insert TEST")
    void insUserFollow() {
        UserFollowReq p1=new UserFollowReq(0,0);
        List<UserFollowEntity> list1=mapper.selUserFollowListForTest(p1); //전체 리스트를 가짐
        //p1을 멤버필드로 넣으면 where절이 들어가지 않아서 전체 리스트를 출력

        UserFollowReq p2=new UserFollowReq(10, 20);
        int affrectedRows=mapper.insUserFollow(p2); //레코드가 들어갔다는 값만 확인 10, 20을 넣었는데 0,0 이 들어가도 성공으로 뜸
        //영향 받은 행 뿐만이 아니라 다른 곳에도 확인을 해야 함 test용 select 필요
        //내가 기대했던 값이 맞는지 확인 //Assertions.assertEquals(기대하는 값,실제 값);
        assertEquals(1,affrectedRows); //import를 static으로 했기 때문
        //실행해도 DB에 들어가지 않음 > 트랜젝션(여러개의 작업을 하나의 작업으로 여김)(+웹개발자에게는 롤백, 커밋) 때문
        //성공이 되더라도 롤백 시키기 때문에 DB에 올라가지 않음(기본적으로 걸려있음)
        //내가 원하는 값이 들어갔는지 확인=select해야 함
        //기존 size와 insert 후 size의 차가 1인지 확인
        List<UserFollowEntity> list2=mapper.selUserFollowListForTest(p1);
        assertEquals(1, list2.size()-list1.size(),"실제 INSERT 되지 않음!");

        //레코드가 있는지 없는지 체크
        List<UserFollowEntity> list3=mapper.selUserFollowListForTest(p2); //PK를 넣었을 때 값이 있거나 없거나
        assertEquals(1, list3.size(), "p2값이 제대로 INSERT 되지 않음"); //값이 있다면 제대로 들어간 것

        //날짜 때문에 객체 비교는 어려워 값 비교
        //???????????얘 왜 넣어?????????
        assertEquals(p2.getFromUserId(), list3.get(0).getFromUserId(),"From이 없음!");
        assertEquals(p2.getToUserId(), list3.get(0).getToUserId(),"To가 없음!");

        //숫자를 바꿔서도 테스트
        UserFollowReq p3=new UserFollowReq(4, 5);
        int affrectedRows2=mapper.insUserFollow(p3);
        assertEquals(1, affrectedRows2,"숫자 바꿨을 때 영향받은 행");
        //리스트 사이즈를 값으로 변수 지정하면 덜 헷갈림
        int recordCountAfterFirstInsert=list2.size();
        List<UserFollowEntity> list4=mapper.selUserFollowListForTest(p1);
        assertEquals(1, list4.size()-recordCountAfterFirstInsert,"실제 INSERT 되지 않음");
    }
    @Test
    @DisplayName("유저 팔로우 select TEST")
    void selUserFollowListForTest(){
        UserFollowReq p1=new UserFollowReq(0,0);//0, 0 -> where절이 없는 쿼리문 실행
        List<UserFollowEntity> list1=mapper.selUserFollowListForTest(p1);
        //검증: 해당 값이 있는지 확인
        //1. 레이블 수 확인
        assertEquals(12, list1.size(), "레코드 수가 다르다.");

        UserFollowEntity record0=list1.get(0);
        assertEquals(2, record0.getToUserId(),"0번 to User");
        assertEquals(1, record0.getFromUserId(),"0번 from User");
        assertEquals(new UserFollowEntity(1, 3, "2024-05-20 11:52:44"),
                list1.get(1),"1번 레코드 값이 다름");//HashCode의 효과

        //2. from =1
        UserFollowReq p2=new UserFollowReq(1,0);
        List<UserFollowEntity> list2=mapper.selUserFollowListForTest(p2);
        assertEquals(4,list2.size(), "2 레코드 수가 다르다.");
        assertEquals(new UserFollowEntity(1,2,"2024-05-20 11:52:39")
                ,list1.get(0),"0번 레코드 다름");

        //3. fromUser=300 레코드가 없어야 함
        UserFollowReq p3=new UserFollowReq(300, 0);
        List<UserFollowEntity> list3=mapper.selUserFollowListForTest(p3);
        assertEquals(0, list3.size(),"레코드가 없어야 됨");

        //4. toUserId=1
        UserFollowReq p4=new UserFollowReq(0, 1);
        List<UserFollowEntity> list4=mapper.selUserFollowListForTest(p4);
        assertEquals(2, list3.size(),"레코드의 수가 다름");
        assertEquals(new UserFollowEntity(2, 1, "2024-05-20 11:52:39"),
                list4.size(), "많다");
    }

    @Test
    void delUserFollow() {
        UserFollowReq originCountParam=new UserFollowReq(0,0);
        List<UserFollowEntity> list=mapper.selUserFollowListForTest(originCountParam);
        int originalSize=list.size();
        //assertEquals(12, originalSize);

        //1. 없는 PK삭제
        UserFollowReq p2=new UserFollowReq(10,20);
        int affrectedRows2=mapper.delUserFollow(p2);
        assertEquals(0,affrectedRows2,"삭제가 되면 안되나 삭제됨");

        //2. (1,2)삭제
        UserFollowReq p3=new UserFollowReq(1,2);
        List<UserFollowEntity> p3List=mapper.selUserFollowListForTest(p3);
        assertEquals(1, p3List.size());//존재하는지 확인

        int affectedRows3=mapper.delUserFollow(p3);
        assertEquals(1, affectedRows3,"삭제 이상"); //존재하는 걸 지웠는데 영향받은 행이 1이 아님

        List<UserFollowEntity> totalList2=mapper.selUserFollowListForTest(originCountParam);
        int recordContFirst=totalList2.size();
        assertEquals(1, originalSize-recordContFirst); //영향 받은 행이 1인데 레코드 숫자에 차이가 없음

        List<UserFollowEntity> p3List2=mapper.selUserFollowListForTest(p3);
        assertEquals(0, p3List2.size()); //존재한다면 무언가 다른 행을 지웠음

    }
}
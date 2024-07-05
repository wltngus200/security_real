package com.green.greengram.user;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.user.model.*;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import({CustomFileUtils.class, UserServiceImpl.class})
@TestPropertySource(
        properties = {
                "file.directory=D:/2024_BACK_JI/download/greengram_tdd/"
        }
    )
class UserServiceTest {
    @Value("${file.directory}")
    String uploadPath;
    @MockBean //호출이 되었는지 아닌지 알 수 있
    private UserMapper mapper;
    @Autowired
    private CustomFileUtils utils;

    @Autowired
    private UserService service;

    @Test
    void postUser() throws IOException {//PK경로로 파일을 갖다놓는지 확인
        String p1Pw = "abc";
        SignUpPostReq p1 = new SignUpPostReq();
        p1.setUserId(100);
        p1.setUpw(p1Pw);//실제로 폴더가 생성됨

        //mapper에게 임무부여
        given(mapper.postUser(p1)).willReturn(1);

        SignUpPostReq p2 = new SignUpPostReq();
        p2.setUserId(150);
        p2.setUpw("def");

        //mapper에게 임무부여
        given(mapper.postUser(p2)).willReturn(2);

        MultipartFile fm1 = new MockMultipartFile("pic", "1c8ce0cd-347f-4a99-91e0-18c80ee70539.jpg", "image/jpg",
                new FileInputStream("D:/2024_BACK_JI/download/greengram_tdd/user/6/1c8ce0cd-347f-4a99-91e0-18c80ee70539.jpg"));
        //첫 이름: 메소드에 파라미터의 변수명(컨트롤러에서 파일을 받을 때 쓰는 이름 지금은 상관 X)
        //두 번쨰 : 오리지널 파일 이름
        //확장자
        //new 객체 생성 파일의 절대 주소값

        assertEquals(1, service.postUser(fm1, p1));
        p1.getPic();//레퍼런스 타입에 대한 이해 필요(랜덤하게 생성된 폴더 이름 받음)
        //파일이 내가 원하는 장소에 만들어졌는지
        File savedFile1 = new File(uploadPath, String.format("%s/%d/%s", "user", p1.getUserId(), p1.getPic()));
        assertTrue(savedFile1.exists(), "파일이 만들어지지 않음"); //boolean 타입 파일이 잘 존재한다
        savedFile1.delete(); //디버깅으로 하면 생기고 사라지는 것을 볼 수 있다

        assertNotEquals(p1Pw, p1.getUpw()); //달라야 TRUE 암호화로 인해 달라졌기 때문

        verify(mapper).postUser(p1);

        int result = service.postUser(fm1, p2);
        File savedFile2 = new File(uploadPath, String.format("%s/%d/%s", "user", p2.getUserId(), p2.getPic()));
        assertEquals(2, result);
        assertTrue(savedFile2.exists());
        savedFile2.delete();

    }

//    @Test
//    void postSignIn() {
//        SignInPostReq req1 = new SignInPostReq();
//        req1.setUid("aa123");
//        req1.setUpw("aa123");
//        String hashedPw = BCrypt.hashpw(req1.getUpw(), BCrypt.gensalt());
//        SignInPostReq req2 = new SignInPostReq();
//        req2.setUid("bb123");
//        req2.setUpw("bb123");
//        String hashedPw2 = BCrypt.hashpw(req2.getUpw(), BCrypt.gensalt());
//
//        //임무부여
//        User user1 = new User(111, req1.getUid(), hashedPw, "홍길동1", "사진1.jpg", null, null);
//        //given(mapper.getUserId(req1.getUid())).willReturn(user1);
//        //가짜 mapper는 호출이 되었는지 확인 가능 //static은 안 됨 //가짜 static을 만드는 방법
//        User user2 = new User(222, req2.getUid(), hashedPw2, "홍길동2", "사진2.jpg", null, null);
//        //given(mapper.getUserId(req2.getUid())).willReturn(user2);
//
//        try (MockedStatic<BCrypt> mockedStatic = mockStatic(BCrypt.class)) {
//            //try with resource: 괄호안에는 특정한 것을 상속했을 때만 적을 수 있음
//
//            mockedStatic.when(() -> BCrypt.checkpw(req1.getUpw(), user1.getUpw())).thenReturn(true);
//           // SignInRes res1 = service.postSignIn(req1);
//            assertEquals(user1.getUserId(), res1.getUserId(), "1. userid 다름");
//            assertEquals(user1.getNm(), res1.getNm(), "1.nm 다름");
//            assertEquals(user1.getPic(), res1.getPic(), "1.pic 다름");
//
//            mockedStatic.when(() -> BCrypt.checkpw(req2.getUpw(), user2.getUpw())).thenReturn(true);
//            //SignInRes res2 = service.postSignIn(req2);
//            assertEquals(user2.getUserId(), res2.getUserId(), "2. userid 다름");
//            assertEquals(user2.getNm(), res2.getNm(), "2.nm 다름");
//            assertEquals(user2.getPic(), res2.getPic(), "2.pic 다름");
//
//            mockedStatic.verify(() -> BCrypt.checkpw(req2.getUpw(), user2.getUpw()));
//
//        }
//        //예외처리
//        SignInPostReq req3 = new SignInPostReq();
//        req3.setUid("cc123");
//        given(mapper.getUserId(req3.getUid())).willReturn(null);
//
//        Throwable ex1 = assertThrows(RuntimeException.class, () -> {
//            service.postSignIn(req3);
//        }, "아이디 없음 예외처리 안 함");
//        assertEquals("(′д｀σ)σ 너는 누구야?", ex1.getMessage(), "아이디 없음 에러메세지가 다름");
//
//
//
//        SignInPostReq req4=new SignInPostReq();
//        req4.setUid("dd123");
//        req4.setUpw("dd123");
//        String hashedUpw4= BCrypt.hashpw("777", BCrypt.gensalt());//다른 비번
//        User user4=new User(100, req4.getUid(),hashedUpw4, "홍길동", "사진4.jpg", null,null);
//
//        given(mapper.getUserId(req4.getUid())).willReturn(user4);
//        Throwable ex2=assertThrows(RuntimeException.class,()-> {
//            service.postSignIn(req4);
//        }, "비밀번호 예외처리 안 함");
//        assertEquals("(o゜▽゜)o☆ 비밀번호 틀렸쪄", ex2.getMessage(), "비밀번호 다름, 에러메세지 다름");
//    }

    @Test
    void getUserInfo() {//기존 파일을 지우고 새로 넣는 것까지
        UserInfoGetReq p1= new UserInfoGetReq(1);
        UserInfoGetRes result1=new UserInfoGetRes();
        result1.setNm("test1"); //객체들을 구분하기 위한 값
        given(mapper.selProfileUserInfo(p1)).willReturn(result1); //가짜 매퍼에게 임무를 줌
        UserInfoGetReq p2= new UserInfoGetReq(1);
        UserInfoGetRes result2=new UserInfoGetRes();
        result1.setNm("test2"); //객체들을 구분하기 위한 값
        given(mapper.selProfileUserInfo(p2)).willReturn(result2); //가짜 매퍼에게 임무를 줌

        UserInfoGetRes res1=service.getUserInfo(p1);
        assertEquals(result1, res1);
        UserInfoGetRes res2=service.getUserInfo(p2);
        assertEquals(result2, res2);

    }

    @Test//전체 리스트를 가져온다-> 왜......??
    void patchProfilePic() throws IOException {//실제 customfileUtils로 만듦
        //서비스를 테스트 : 새로운 파일이 올라오면 PK를 이용해 기존에 있던 폴더를 지우고 다시 만들어 파일 넣음
        long signedUserId1 = 500;
        final String ORIGIN_FILE_PATH = String.format("%stest/%s", uploadPath, "a.jpg"); //원본의 위치
        String midPath1 = String.format("%suser/%d", uploadPath, signedUserId1); //transfer할 위치
        File originFile = new File(ORIGIN_FILE_PATH);
        File copyFile1 = new File(midPath1, "a.jpg"); //target

        utils.deleteFolder(midPath1);
        utils.makeFolders("user/" + signedUserId1);
        Files.copy(originFile.toPath(), copyFile1.toPath(), StandardCopyOption.REPLACE_EXISTING);

        UserProfilePatchReq p1 = new UserProfilePatchReq();//호출을 위한 파라미터 세팅
        p1.setSignedUserId(signedUserId1);

        MultipartFile fm1 = new MockMultipartFile("pic", "b.jpg", "image/jpg",
                new FileInputStream(String.format("%stest/b.jpg", uploadPath)));
        p1.setPic(fm1);

        String fileNm1=service.patchProfilePic(p1); //null이 아닌지만 체크 랜덤 파일명만 넘어옴
        assertNotNull(fileNm1,"1. 파일 명이 Null이 넘어왔음");
        //midPath1폴더 존재, 해당 폴더 파일 1개 존재, 그 파일의 파일 명이 fileNm1과 같아야 함
        File p=new File(midPath1); //D:\2024_BACK_JI\download\greengram_tdd\\user\500
        File[] p2=p.listFiles();

        assertEquals(true, p.exists(),"1. midPath1 폴더가 존재하지 않음");
        assertEquals(1, p2.length,"2. 해당 폴더에 파일이 1개가 아님");
        assertEquals(1, p.listFiles().length,"2. 선생님 방법");
        assertEquals(fileNm1, p2[0].getName(), "3. 파일 이름이 다름");
        File file1=p.listFiles()[0];
        assertEquals(fileNm1, file1.getName(),"3. 선생님 방법");

        verify(mapper).updProfilePic(p1);

        //------------------------다른 방법으로 복사 시도----------------------
        //MultipartFile mf1=new MockMultipartFile("pic","","image/jpg",new FileInputStream(""));



    }
}



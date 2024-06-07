package com.green.greengram.feed;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.feed.model.*;
import com.green.greengram.feedcomment.FeedCommentServiceImpl;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Import({FeedServiceImpl.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(
        properties = {
                "file.directory=D:/2024_BACK_JI/download/greengram_tdd/"
        }
)
class FeedServiceTest {
    @Value("${file.directory}")
    String uploadPath;
    @MockBean //service가 객체화 할 때 주소값이 필요하기 때문에
    FeedMapper mapper;
    @MockBean
    CustomFileUtils utils;

    @Autowired //객체 주소 값을 DI 해달라 (사전에 객체화가 되어야함)
            //RequiredArgsConstructor + final
    FeedService service;

    @Test
    void postFeed() throws Exception{
        FeedPostReq p=new FeedPostReq();
        //feedId값 주입 필요
        //  -> service 내에서 사용하기 때문에
        //  + 실제는 mapper의 메소드 호출 시 keyProperty로 받아옴 지금은 가짜라 안 넣어주니 넣어주기
        p.setFeedId(10);
        List<MultipartFile> pics=new ArrayList();
        MultipartFile fm1 = new MockMultipartFile("pic", "a.jpg", "image/jpg",
                new FileInputStream(String.format("%stest/a.jpg", uploadPath)));
        MultipartFile fm2 = new MockMultipartFile("pic", "b.jpg", "image/jpg",
                new FileInputStream(String.format("%stest/b.jpg", uploadPath)));
                //FileInputStream이 오류를 던지고 있음(클래스 객체화를 위한 생성자 메소드 호출)
        pics.add(fm1); pics.add(fm2);
        String randomFileName1="ab12.jpg";
        String randomFileName2="12ab.jpg";
        String[] randomFileArr={randomFileName1, randomFileName2};
        given(utils.makeRandomFileName(fm1)).willReturn(randomFileName1);
        given(utils.makeRandomFileName(fm2)).willReturn(randomFileName2);

        FeedPostRes res=service.postFeed(pics, p);
        verify(mapper).postFeed(p);

        //utils리턴이 없기 때문에 given?으로 임무를 줄 필요는 없음
        String path=String.format("feed/%s", p.getFeedId()); //이거 user넣었을 때 왜 오류남?
        verify(utils).makeFolders(path);

        FeedPostPicReq req= FeedPostPicReq.builder()
                .feedId(p.getFeedId()).build();

        for(int i=0;i<pics.size();i++){
            MultipartFile f=pics.get(i);
            verify(utils).makeRandomFileName(f);
            String fileNm=randomFileArr[i];
            String target=String.format("%s/%s",path, randomFileArr[i]);
            req.getFileNames().add(fileNm);
            verify(utils).transferTo(f, target);
        }
        verify(mapper).postFeedPics(req);

        FeedPostRes watedRes=FeedPostRes.builder()//주소값이 다르기 때문에 EqualsAndHashCode 필요
                        .feedId(req.getFeedId())
                        .pics(req.getFileNames())
                            .build();
        assertEquals(watedRes, res, "뭐지");

    }

    @Test
    void getFeed() {
        FeedGetReq req=new FeedGetReq(1, 10,2,3L);
        List<FeedGetRes> list=new ArrayList(); //리스트
        FeedGetRes fgr1=new FeedGetRes(); //0번
        FeedGetRes fgr2=new FeedGetRes(); //1번
        list.add(fgr1); list.add(fgr2);
        fgr1.setContents("a"); fgr1.setFeedId(1); //given
        fgr2.setContents("b"); fgr2.setFeedId(2);

        given(mapper.getFeed(req)).willReturn(list);

        //사진이름 리스트
        List<String> pics1=new ArrayList(); //0번 피드의 사진
        pics1.add("안녕"); pics1.add("반가워");
        List<String> pics2=new ArrayList(); //1번 피드의 사진
        pics2.add("Hello"); pics2.add("Hi~"); pics2.add("nice");

        //feedId에 맞는 사진 리스트를 가져옴
        given(mapper.getFeedPicsByFeedId(fgr1.getFeedId())).willReturn(pics1); //feedId 1
        given(mapper.getFeedPicsByFeedId(fgr2.getFeedId())).willReturn(pics2); //feedId 2

        //commentList
        List<FeedCommentGetRes> comList1=new ArrayList();
        FeedCommentGetRes comRes1=new FeedCommentGetRes();
        FeedCommentGetRes comRes2=new FeedCommentGetRes();
        FeedCommentGetRes comRes3=new FeedCommentGetRes();
        FeedCommentGetRes comRes4=new FeedCommentGetRes();
        comList1.add(comRes1); comList1.add(comRes2);
        comList1.add(comRes3); comList1.add(comRes4);
        comRes1.setComment("b1");
        comRes2.setComment("b2");
        comRes3.setComment("b3");
        comRes4.setComment("b4");

        List<FeedCommentGetRes> comList2=new ArrayList();
        FeedCommentGetRes comRes11=new FeedCommentGetRes();
        FeedCommentGetRes comRes22=new FeedCommentGetRes();
        comList2.add(comRes11); comList2.add(comRes22);
        comRes11.setComment("a1");
        comRes22.setComment("a2");

        given(mapper.getFeedComment(fgr1.getFeedId())).willReturn(comList1); //0번 feed에는 이 댓글 목록
        given(mapper.getFeedComment(fgr2.getFeedId())).willReturn(comList2);

        List<FeedGetRes> res=service.getFeed(req); //service에 넣었을 때의 리턴 값
        //내가 service에 req, (가짜)mapper에 req를 넣었을 때, 리턴을 명령한 리스트
        assertEquals(list.size()/*mockMapper*/, res.size()/*service*/, "리턴값 다름");//내가 만든 가짜 피드와 얘가 리턴할 값의 일치
        verify(mapper).getFeed(req); //mock Mapper에 실제로 이 값을 넣은 메소드가 실행됨?
        for(int i=0; i<list.size();i++){ //foreach 가능
            verify(mapper).getFeedPicsByFeedId(list.get(i).getFeedId());
            verify(mapper).getFeedComment(list.get(i).getFeedId());
        }

        FeedGetRes actualItem=res.get(0); //0번 피드 fgr1을 넣었을 때 값
        //아래 3개는 같은 것
        assertEquals(pics1.size(), actualItem.getPics().size(), "fgr1의 이미지 값이 다름");
        assertEquals(pics1, actualItem.getPics(), "fgr1의 이미지 값이 다름");//둘은 같은 주소값을 가짐
        assertEquals(comList1, actualItem.getComments(), "frg1의 댓글 다름");
        assertEquals(3, comList1.size()); //하나가 지워졌을 것
        assertEquals(1, actualItem.getIsMoreComment());


        //너무 많앙
        //뭐가 틀린거징~~~

    }

}
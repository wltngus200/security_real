package com.green.greengram.feed;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.feed.model.FeedPostPicReq;
import com.green.greengram.feed.model.FeedPostReq;
import com.green.greengram.feed.model.FeedPostRes;
import com.green.greengram.feedcomment.FeedCommentServiceImpl;
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
    }

}
package com.green.greengram.feed;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.feed.model.*;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper mapper;
    private final CustomFileUtils customFileUtils;

    @Transactional
    FeedPostRes postFeed(List<MultipartFile> fileNames, FeedPostReq p){
        int result=mapper.postFeed(p);//내용과 위치를 데이터베이스에 올림
        String path=String.format("feed/%s", p.getFeedId());
        customFileUtils.makeFolders(path);//폴더 생성

        //DB에 사진  저장 //Builder가 있다!
        //사진을 올리기 위해 요구되는 정보는 multipartfile과 FeedPostReq에 존재
        //그것을 재구성 해서 PicReq를 제조 Req에는 이미 객체화 되어있다
        FeedPostPicReq req= FeedPostPicReq.builder().feedId(p.getFeedId()).build();//PK값 세팅
        for(MultipartFile pic:fileNames) {
            String fileName= customFileUtils.makeRandomFileName(pic);
            String target=String.format("%s/%s", path, fileName);
            try {
                customFileUtils.transferTo(pic, target);
                req.getFileNames().add(fileName);
            }catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException(".·´¯`(>▂<)´¯`·. 업로드에 실패해쪙");
            }
        }mapper.postFeedPics(req);
        return FeedPostRes.builder().feedId(req.getFeedId()).pics(req.getFileNames()).build();
    }FeedPostRes res= FeedPostRes.builder().build();
    //for와 try의 위치가 다르지만 정상작동

    List<FeedGetRes> getFeed(FeedGetReq p){
        log.info("{}",p);
        List<FeedGetRes> list=mapper.getFeed(p);
        log.info("{}",p);
        for(FeedGetRes res:list){
            //사진
            List<String> pics=mapper.getFeedPics(res.getFeedId());
            res.setPics(pics);

            //코멘트
            List<FeedCommentGetRes> comments=mapper.getFeedComments(res.getFeedId());
            res.setComments(comments);

            //좋아요

            //리스트를 받아와야 for문이 가능한데, 그럼 pic이랑 comment를 못 받아와
        }

        return list;
    }
}

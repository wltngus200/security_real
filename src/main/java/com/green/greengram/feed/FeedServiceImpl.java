package com.green.greengram.feed;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.feed.model.*;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.green.greengram.common.GlobalConst.COMMENT_SIZE_PER_FEED;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl {
    private final FeedMapper mapper;
    private final CustomFileUtils customFileUtils;

    @Transactional
    public FeedPostRes postFeed(List<MultipartFile> fileNames, FeedPostReq p){
        int result=mapper.postFeed(p);//내용과 위치를 데이터베이스에 올림

        if(fileNames==null){
            return FeedPostRes.builder()
                    .feedId(p.getFeedId())
                    .build();
        }


        //DB에 사진  저장 //Builder가 있다!
        //사진을 올리기 위해 요구되는 정보는 multipartfile과 FeedPostReq에 존재
        //그것을 재구성 해서 PicReq를 제조 Req에는 이미 객체화 되어있다
        FeedPostPicReq req= FeedPostPicReq.builder()
                            .feedId(p.getFeedId()).build();//PK값 세팅
        try {
            String path=String.format("feed/%s", p.getFeedId());
            customFileUtils.makeFolders(path);//폴더 생성

            for(MultipartFile pic:fileNames) {
                String fileName= customFileUtils.makeRandomFileName(pic);
                String target=String.format("%s/%s", path, fileName);
                customFileUtils.transferTo(pic, target);
                req.getFileNames().add(fileName);
            }mapper.postFeedPics(req);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(".·´¯`(>▂<)´¯`·. 업로드에 실패해쪙");
        }
        return FeedPostRes.builder().feedId(req.getFeedId()).pics(req.getFileNames()).build();
    }

    List<FeedGetRes> getFeed(FeedGetReq p){
        List<FeedGetRes> list=mapper.getFeed(p);
        for(FeedGetRes res:list){
            //사진
            List<String> pics=mapper.getFeedPicsByFeedId(res.getFeedId());
            res.setPics(pics);

            //코멘트
            List<FeedCommentGetRes> comments=mapper.getFeedComment(res.getFeedId());
            if(comments.size()== COMMENT_SIZE_PER_FEED){
                res.setIsMoreComment(1);
                comments.remove(COMMENT_SIZE_PER_FEED-1);
            }
            res.setComments(comments);
        }

        return list;
    }

    int deleteFeed(long feedId){
        String.format("");
        customFileUtils.deleteFolder("");
        return mapper.deleteFeed(feedId);
    }
}

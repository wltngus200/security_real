package com.green.greengram.feed.model;

import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FeedGetRes {
    private long feedId;
    private long writerId;
    private String writerNm;
    private String writerPic;
    private String contents;
    private String location;
    private String createdAt;
    private int isFav;

    private List<FeedCommentGetRes> comments; //1개를 빼고 담아야됨
    private int isMoreComment; //1개
    private List<String> pics;
    //피드별 사진 개수 제각각 n+1이슈 //현재 단계에선 Select분리
}

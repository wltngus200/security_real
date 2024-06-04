package com.green.greengram.feed.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
//역할: My batis에 보내는 값 DB에 보내는 쿼리문
@Getter
//빌더가 있다!
@Builder //어레이리스트의 객체주소값 담아주지 X -> 아래 에노테이션
@EqualsAndHashCode
public class FeedPostPicReq {
    private long feedId;
    @Builder.Default //builder로 ArrayList를 초기화 할 경우 null이 되기 때문에 들어감
    //new하지 않고 Default도 지우고 service에서 새로 객체 생성할 경우 필요 없음
    private List<String> fileNames=new ArrayList();
}

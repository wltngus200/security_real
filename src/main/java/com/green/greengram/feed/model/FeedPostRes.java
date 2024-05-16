package com.green.greengram.feed.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
//역할: 프론트에 보냄, JSON+응답
//작업에 따라 분리하는 것이 좋음->보내는 전용, 응답하는 전용
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedPostRes {//프론트에서 필요한 정보
    //피드를 업로드 하고 바로 보고 싶을 때 내가 보낸 contents, location이 아닌 값(=내가 모르는 값)
    private long feedId; //PK값은 올라가고 나서야 알 수 있다
    private List<String> pics; //랜덤한 파일명을 모른다
}

package com.green.greengram.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor//setter를 빼서 VO형식
public class User {
    private long userId;
    private String uid;
    private String upw;
    private String nm;
    private String pic;
    private String createdAt;
    private String updatedAt;

//setter도 없고 생성자가 없는 클래스는 존재 이유가 없다 null이거나 0이거나
    //setter가 없을 시 마이바티스와 함께 쓸 때 주의 점
    //service의 select(Mapper)의 결과를 담는다
    //이름으로 매칭해서 값 넣지 X "순서"에 따라 다름 AS도 필요 없다
    //setter일때는 AS필요 "생성자"일 때는 필요 X
    //생성자=순서 중요!
}

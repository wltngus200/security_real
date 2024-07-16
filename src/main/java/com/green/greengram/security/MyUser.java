package com.green.greengram.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

//@JsonIgnoreProperties(ignoreUnknown = true)//기본 값 false //현재 미사용
//Json에 더 많은 속성이 있는데 MyUser에 없는 멤버 필드는 무시하고 객체 생성

/*
  {
    "userId" : 10,
    "role" : "ROLE_USER",
    "addr" : "대구시"
  }

  위 JSON을 MyUser로 파싱할 떄 에러 발생>>>addr값을 담을 수 없기 때문
  위 에노테이션으로 addr을 무시하고 객체 생성을 하게 한다
*/

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor //JSON > Object 할 때 필요
@Builder
public class MyUser {
    private long userId; //로그인 한 사용자의 PK값
    private List<String> roles; //사용자의 권한, ROLE_권한이름
}

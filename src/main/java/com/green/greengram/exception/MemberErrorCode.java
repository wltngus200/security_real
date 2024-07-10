package com.green.greengram.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor //final이 붙은 생성자 생성
public enum MemberErrorCode implements ErrorCode{ //메소드를 구현해야한다는 강제성 name는 enum이 이미 가짐

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),

    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다.");
    //생성자 호출


    private final HttpStatus httpStatus;
    private final String message;
}

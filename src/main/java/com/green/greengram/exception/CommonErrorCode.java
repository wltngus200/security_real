package com.green.greengram.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{ //ERROR CODE를 상속받음 부모 타입인 error code에 들어갈 수 있음
    //interface method를 구현해야한다는 강제성 name는 enum이 이미 가짐

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부에서 에러가 발생하였습니다. 관리자에게 문의해 주세요"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 파라미터입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}

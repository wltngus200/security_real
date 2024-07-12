package com.green.greengram.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

// runtimeException인데 ErrorCode를 implements한 객체 주소값을 감을 수 있는 기능
@Getter
@RequiredArgsConstructor
@ToString
public class CustomException extends RuntimeException {
    private final ErrorCode/*interface*/ errorCode;
}

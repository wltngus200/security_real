package com.green.greengram.exception;

import com.green.greengram.common.model.MyResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@SuperBuilder //부모값까지 builder 자식 객체에도 똑같이 해줘야 함
public class MyErrorResponse extends MyResponse<String>/*(구)ResultDto 다른 타입으로 받았던 걸 String만 가지는 애를 상속*/ {
    private final List<ValidationError> valids; //여러개의 에러메세지 동시 출력

    @Getter
    @Builder
    @RequiredArgsConstructor
    /*inner class가 있을 때 적어 주는 것을 추천- 성능이 좋아짐*/
    public static class ValidationError {
        //Validation 에러가 발생 시, 해당하는 에러 메세지를 표시할 때 사용하는 객체
        private final String field; //validation 에러가 발생된 멤버필드 명
        private final String message; //validation 에러 메세지

        /*생성자 같은 메소드 validationError를 builder로 객체화 하여 그 주소값 리턴*/
        public static ValidationError of(final FieldError fieldError){
            return ValidationError.builder()
                    .field(fieldError.getField()/*멤버 필드명*/)
                    .message(fieldError.getDefaultMessage()/*메세지*/)
                    .build();
        }
    }
}

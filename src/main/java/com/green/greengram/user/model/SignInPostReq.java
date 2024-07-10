package com.green.greengram.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SignInPostReq {
    @Schema(example="mic", description = "유저 아이디")
    @NotBlank(message="아이디는 비울 수 없습니다")
    private String uid;
    //@Schema(example="1212", description = "유저 비밀번호")
    @NotBlank(message="비밀번호는 비울 수 없습니다")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String upw;

    @JsonIgnore
    private String providerType;
}

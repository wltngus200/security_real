package com.green.greengram.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter//VO
@Builder
@NoArgsConstructor//Json으로 바뀔 때 기본 생성자 필요
@AllArgsConstructor
@ToString
public class SignInRes {
    @Schema(example = "1", description = "USER PK")
    private long userId;
    @Schema(example = "홍길동", description = "USER NAME")
    private String nm;
    @Schema(example = "djh43jhdbqh1-@d34", description = "USER IMAGE")
    private String pic;
}

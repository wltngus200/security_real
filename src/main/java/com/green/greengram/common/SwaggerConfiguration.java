package com.green.greengram.common;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;

/*@Configurable
@OpenAPIDefinition(
        info=@Info(
                //스웨거의 상단 수정 //타이틀, 타이틀에 대한 설명, 버전
                title="그린그램",
                description="Greengram with React",
                version="v2"
        )
)//인증처리 관련 세팅도 나중에 추가 예정
public class SwaggerConfiguration {
}
*/
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "그린그램",
                description = "GreenGram with react",
                version = "v3"
        ),
//        End Point 마다 자물쇠 아이콘 생성 ( 로그인 가능 )
        security = @SecurityRequirement(name = "authorization")
)

// JWT 토큰을 swagger 에서 사용하게 만드는 코드
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "authorization",
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "JWT",
        scheme = "Bearer"
)
public class SwaggerConfiguration {}
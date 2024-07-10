package com.green.greengram.user;

import com.green.greengram.common.model.MyResponse;
import com.green.greengram.user.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserControllerImpl {
    private final UserServiceImpl service;

    @PostMapping("sign-up")
    public MyResponse<Integer> postUser(@RequestPart(required = false) MultipartFile mf, @RequestPart SignUpPostReq p){
        int result=service.postUser(mf, p);
        return MyResponse.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultData(result)
                .resultMsg("가입 ╰(*°▽°*)╯ 완료")
                .build();
    }
    @PostMapping("sign-in")
    public MyResponse<SignInRes> postSignIn(HttpServletResponse res, @Valid @RequestBody SignInPostReq p){
        SignInRes result=service.postSignIn(res, p);
        return MyResponse.<SignInRes>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("♪(´▽｀) 어서 와")
                .resultData(result)
                .build();
    }

    @GetMapping("access-token")
    public MyResponse<Map> getRefreshToken(HttpServletRequest req/*요청만 있으면 됨(이유: 프론트에서 get요청 URL만 넘겨도 필요한 정보-refreshToken-이 넘어옴(cookie 사용))*/){
                                                                                          //refreshToken을 로그인을 성공하면 cookie에 담았기 때문 cookie는 요청마다 넘어옴
        Map map=service.getAccessToken(req);
        return MyResponse.<Map>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("Access Token 발급")
                .resultData(map)
                .build();
    }


    @GetMapping
    public MyResponse<UserInfoGetRes> getUserInfo(@ParameterObject @ModelAttribute UserInfoGetReq p){
        UserInfoGetRes result=service.getUserInfo(p);
        return MyResponse.<UserInfoGetRes>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("( ⓛ ω ⓛ *) 파칭")
                .resultData(result)
                .build();
    }

    @PatchMapping(value="pic", consumes="multipart/form-data")//consumes 빼도 가능
    public MyResponse<String> patchProfilePic(@ModelAttribute UserProfilePatchReq p){//모델 어트리뷰트=폼데이터 형식 JSON안 보냄
        //순수하게 HTML만으로 데이터를 전송할 수 있는 것이 폼데이터
        String result = service.patchProfilePic(p);//파일 명 리턴
        return MyResponse.<String>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("(❤´艸｀❤)")
                .resultData(result)
                .build();
    }
}

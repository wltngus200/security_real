package com.green.greengram.user;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.user.model.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService service;

    @PostMapping("sign-up")
    public ResultDto<Integer> postUser(@RequestPart(required = false) MultipartFile mf, @RequestPart SignUpPostReq p){
        int result=service.postUser(mf, p);
        return ResultDto.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultData(result)
                .resultMsg("가입 ╰(*°▽°*)╯ 완료")
                .build();
    }
    @PostMapping("sign-in")
    public ResultDto<SignInRes> postSignIn(@RequestBody SignInPostReq p){
        SignInRes result=service.postSignIn(p);
        return ResultDto.<SignInRes>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("♪(´▽｀) 어서 와")
                .resultData(result)
                .build();
    }
    @GetMapping
    public ResultDto<UserInfoGetRes> getUserInfo(@ParameterObject @ModelAttribute UserInfoGetReq p){
        UserInfoGetRes result=service.getUserInfo(p);
        return ResultDto.<UserInfoGetRes>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg("( ⓛ ω ⓛ *) 파칭")
                .resultData(result)
                .build();
    }
}

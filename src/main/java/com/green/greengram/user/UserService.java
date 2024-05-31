package com.green.greengram.user;

import com.green.greengram.user.model.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    int postUser(MultipartFile mf, SignUpPostReq p);
    SignInRes postSignIn(SignInPostReq p);
    UserInfoGetRes getUserInfo(UserInfoGetReq p);
}

package com.green.greengram.user;

import com.green.greengram.user.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int postUser(SignUpPostReq p);
    User getUserId(String uid);
    UserInfoGetRes selProfileUserInfo(UserInfoGetReq p);
    int updProfilePic(UserProfilePatchReq p);

    List<User> selTest(long userId);//한 개의 값을 보낼 때 변수명 중요 X

}

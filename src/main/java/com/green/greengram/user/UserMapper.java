package com.green.greengram.user;

import com.green.greengram.user.model.SignUpPostReq;
import com.green.greengram.user.model.User;
import com.green.greengram.user.model.UserInfoGetReq;
import com.green.greengram.user.model.UserInfoGetRes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int postUser(SignUpPostReq p);
    User getUserId(String uid);
    UserInfoGetRes selProfileUserInfo(UserInfoGetReq p);
}

package com.green.greengram.userfollow;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.userfollow.model.UserFollowReq;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserFollowController {
    ResultDto<Integer> postUerFollow(UserFollowReq p);
    ResultDto<Integer> deleteUserFollow(UserFollowReq p);
}

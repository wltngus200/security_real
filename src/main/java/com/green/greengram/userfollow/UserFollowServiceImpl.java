package com.green.greengram.userfollow;

import com.green.greengram.userfollow.model.UserFollowReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {
    private final UserFollowMapper mapper;

    public int postUserFollow(UserFollowReq p){
        return mapper.insUserFollow(p);
    }
    public int deleteUserFollow(UserFollowReq p){
        return mapper.delUserFollow(p);
    }
}

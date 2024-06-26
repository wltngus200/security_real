package com.green.greengram.userfollow;

import com.green.greengram.security.AuthenticationFacade;
import com.green.greengram.userfollow.model.UserFollowReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {
    private final UserFollowMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public int postUserFollow(UserFollowReq p){
        p.setFromUserId(authenticationFacade.getLoginUserId());
        return mapper.insUserFollow(p);
    }
    public int deleteUserFollow(UserFollowReq p){
        p.setFromUserId(authenticationFacade.getLoginUserId());
        return mapper.delUserFollow(p);
    }
}

package com.green.greengram.feedfavorite;

import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import com.green.greengram.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedFavoriteServiceImpl implements FeedFavoriteService {
    private final FeedFavoriteMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public int toggleFavorite(FeedFavoriteToggleReq p){
        p.setUserId(authenticationFacade.getLoginUserId());
        int result = mapper.delFeedFavorite(p);
        if(result == 1) {
            return 0;
        }
        return mapper.insFeedFavorite(p);
    }

}

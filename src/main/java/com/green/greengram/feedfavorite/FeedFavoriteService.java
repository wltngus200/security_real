package com.green.greengram.feedfavorite;

import com.green.greengram.feedfavorite.model.FeedFavoriteToggleReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedFavoriteService {
    private final FeedFavoriteMapper mapper;

    int toggleFavorite(FeedFavoriteToggleReq p){
        int result;
        try{
            mapper.insFeedFavorite(p);
            result=1;
        }catch(Exception e){
            mapper.delFeedFavorite(p);
            result=0;
        }
        return result;
    }//좋아요 처리 & 취소 메세지 띄우기 불가능

}

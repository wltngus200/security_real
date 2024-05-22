package com.green.greengram.feed.model;

import com.green.greengram.common.GlobalConst;
import com.green.greengram.common.model.Paging;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.BindParam;

@Getter
@Setter
@ToString
public class FeedGetReq extends Paging {
    @Schema(name="signed_user_id")
    private long signedUserId;

    @Schema(name="profile_user_id", description="프로필 사용자 ID(Not Required), 프로필 화면에서 사용")
    private Long profileUserId; //사용자의 프로필에 들어갔을 때 사용 그 사용자가 쓴 피드만 보고 싶을 때(=알파 정보)

    public FeedGetReq(Integer page, Integer size,
                      @BindParam("signed_user_id") long signedUserId, //Null이 들어오면 에러
                      @BindParam("profile_user_id") Long profileUserId){
        super(page, size ==null? GlobalConst.FEED_PAGING_SIZE:size);
        this.signedUserId=signedUserId;
        this.profileUserId=profileUserId;
    }
}

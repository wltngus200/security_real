package com.green.greengram.common;

import com.green.greengram.user.model.UserInfo;
import com.green.greengram.user.model.UserInfoRoles;

import java.util.ArrayList;
import java.util.List;

public class MyCommonUtils {
    //CookieUtils는 Bean등록했지만 이건 아님
    //select 한 버능로 해결, 해당 유저의 권한에 따라 레코드가 1개만 넘어올 수도 여러개 일수도
    public static UserInfoRoles /*변환시키다*/convertToUserInfoRoles(List<UserInfo> list){
        if(list==null || list.size()==0){
            return null;
        }
        UserInfoRoles userInfoRoles=new UserInfoRoles();
        List<String> roles=new ArrayList();
        UserInfo userInfo=list.get(0); //권한이 몇개인지는 모르지만 0번방은 항상 존재
        //그 정보를 뽑아 중복된 값을 담고

        userInfoRoles.setUserId(userInfo.getUserId());
        userInfoRoles.setCreatedAt(userInfo.getCreatedAt());
        userInfoRoles.setUpdatedAt(userInfo.getUpdatedAt());
        userInfoRoles.setUid(userInfo.getUid());
        userInfoRoles.setUpw(userInfo.getUpw());
        userInfoRoles.setNm(userInfo.getNm());
        userInfoRoles.setPic(userInfo.getPic());
        userInfoRoles.setRoles(roles); //유일하게 다른 값

        for(UserInfo ui:list){
            roles.add(ui.getRole());
        }
        return userInfoRoles;
    }

}

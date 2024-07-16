package com.green.greengram.security.oauth2;

import com.green.greengram.common.MyCommonUtils;
import com.green.greengram.security.MyUserDetails;
import com.green.greengram.security.MyUserOAuth2VO;
import com.green.greengram.security.SignInProviderType;
import com.green.greengram.user.UserMapper;
import com.green.greengram.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService {
    private final UserMapper mapper;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    /*
    OAuth2 제공자(구글, 페이스북, 카카오, 네이버 등)로부터 Access-token 받은 후 loadUser메소드 호출(스프링 시큐리티에 구현 되어있음)
    request에 사용자의 access-token이 저장되어있음, OAuth2 제공자로부터 사용자 정보를 가져온다(이미 구현되어있음 super.loadUser(userRequest);)
    Oauth2User(interface)객체(인터페이스를 구현한 인증 객체)를 정리해서 리턴
    Front end 로그인 하고 싶은 소셜 로그인 아이콘 클릭(redirect-로그인 완료 후 다시 돌아올 FE의 주소값- 정보 전달)
    >백엔드에 요청 ”나 ~소셜 로그인 하고 싶어”
    > 백엔드는 redirect (OAuth 제공자 로그인 화면)
    >해당 제공자의 아이디/비번을 작성 후 로그인 처리
    >제공자는 인가코드를 백엔드에게 보내준다
    >백엔드는 인가코드를 가지고 access-token을 발급 받는다
    >access-token으로 사용자 정보(scope-아래 사진 참고-에 작성한 내용)를 받는다
    >이후는 자체 로그인 처리
    */

    @Override
    //access 토큰 발행 후 실행 되는 메소드
    public OAuth2User loadUser(OAuth2UserRequest userRequest/*access-token 받음*/) throws OAuth2AuthenticationException{
        try{
            return this.process/*아래 메소드 호출*/(userRequest);
        }catch(AuthenticationException e){
            throw e;
        }catch(Exception e){
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }
    private OAuth2User process(OAuth2UserRequest userRequest/*access소지*/){
        OAuth2User oAuth2User=super.loadUser(userRequest);//제공자로부터 (scope를 통해) 사용자 정보 얻음
        //각 소셜 플랫폼에 맞는 enum 타입을 얻는다
        SignInProviderType/*enum*/ signInProviderType/*구글인지 카카오인지 네이버인지*/
                =SignInProviderType/*enum*/.valueOf(userRequest
                    .getClientRegistration()//내가 어디에 날리는 건지(어느 플랫폼인지)
                    .getRegistrationId()//해당 정보를 문자열로 return
                    .toUpperCase());//대문자로 변경
        //각 소셜 플랫폼에 맞는 UserInfo //규격화 된 UserInfo 객체로 변환 하여 가져옴

        OAuth2UserInfo/*아이디 이메일 값 통일 되어있음*/ oAuth2UserInfo
                = oAuth2UserInfoFactory/*규격화 시키는 클래스*/.getOAuth2UserInfo(signInProviderType, oAuth2User.getAttributes()/*Data->hashMap형식으로 바뀜*/);
        //기존에 회원가입 되어있는가 체크
        SignInPostReq signInParam = new SignInPostReq();
                            //oAuth2UserInfo.getId(); //유니크한 값을 얻을 수 있다
        signInParam.setUid(oAuth2UserInfo.getId()); //플랫폼에서 넘어오는 유니크값(항상 같은 값이며 다른 사용자와 구별되는 유니크 값)
        signInParam.setProviderType(signInProviderType.name());
        //null check
        List<UserInfo> userInfoList= mapper.getUserId(signInParam);

        UserInfoRoles userInfoRoles= MyCommonUtils.convertToUserInfoRoles(userInfoList);

        if(userInfoRoles==null){ //회원가입 처리
            SignUpPostReq signUpParam=new SignUpPostReq();
            signUpParam.setProviderType(signInProviderType/*google로 하면 GOOGLE*/);
            signUpParam.setUid(oAuth2UserInfo.getId());
            signUpParam.setNm(oAuth2UserInfo.getName());
            signUpParam.setPic(oAuth2UserInfo.getProfilePicUrl());
            int result =mapper.postUser(signUpParam);

            userInfoRoles=new UserInfoRoles();
            userInfoRoles.setUserId(signUpParam.getUserId());
            userInfoRoles.setNm(signUpParam.getNm());
            userInfoRoles.setPic(signUpParam.getPic());
        }//소셜 로그인 정보를 제공한 사이트 ex.네이버에서 프로필 사진을 변경해서 기존 사진이 사라진 경우 에러 야기(Notion 0708)
        else{
            if((userInfoRoles.getPic()==null ||!userInfoRoles.getPic().startsWith("http"))
                    && !userInfoRoles.equals(oAuth2UserInfo.getProfilePicUrl())){ //프로필이 변경되었다면
                //프로필 사진 update

            }
        }

        List<String> roles=new ArrayList();
        roles.add("ROLE_USER");

        MyUserOAuth2VO myUserOauth2VO
                =new MyUserOAuth2VO(userInfoRoles.getUserId(), roles, userInfoRoles.getNm(), userInfoRoles.getPic());

        MyUserDetails signInUser=new MyUserDetails();
        signInUser.setMyUser(myUserOauth2VO); //상속관계이기때문에 저장 가능

        return signInUser;
    }
}

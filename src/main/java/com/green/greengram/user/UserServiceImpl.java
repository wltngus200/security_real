package com.green.greengram.user;

import com.green.greengram.common.CookieUtils;
import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.common.model.AppProperties;
import com.green.greengram.security.*;
import com.green.greengram.security.jwt.JwtTokenProviderV2;
import com.green.greengram.user.model.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserMapper mapper;
    private final CustomFileUtils customFileUtils;
    //비밀번호 라이브러리를 바꿔도 수정을 안 해도 됨
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProviderV2 jwtTokenProvider;
    private final CookieUtils cookieUtils;
    private final AuthenticationFacade authenticationFacade; //PK값 제공 getLoginUserId();
    private final AppProperties appProperties;

    //SecurityContextHolder > Context
    //          > Authentication(UsernamePasswordAuthenticationToken) > MyUserDetails > MyUser

    public int postUser(MultipartFile mf, SignUpPostReq p){
        //p.setProviderType(SignInProviderType.LOCAL/*자체 회원가입이기 때문에*/.name());//String type의 경우 .name을 적어야함
        p.setProviderType(SignInProviderType.LOCAL/*자체 회원가입이기 때문에*/);
        //암호화
        String hash=passwordEncoder.encode(p.getUpw());
        //String hash= BCrypt.hashpw(p.getUpw(),BCrypt.gensalt());
        p.setUpw(hash);
        String fileName= customFileUtils.makeRandomFileName(mf);
        p.setPic(fileName);

        int result=mapper.postUser(p);//PK값이 필요하기 때문에 먼저 insert

        //사진
        if(mf==null){return result;}

        try{
            String path=String.format("user/%s",p.getUserId());
            customFileUtils.makeFolders(path);
            String target=String.format("%s/%s", path, fileName);
            customFileUtils.transferTo(mf, target);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("사진 업로드 실패");
        }
        return result;
    }

    public SignInRes postSignIn(HttpServletResponse res, SignInPostReq p){
        p.setProviderType(SignInProviderType.LOCAL.name().toUpperCase());
        //p.setProviderType("LOCAL")와 같음
        User user=mapper.getUserId(p);
        if(user==null){throw new RuntimeException("(′д｀σ)σ 너는 누구야?");
        }else if(!BCrypt.checkpw(p.getUpw(),user.getUpw())){
            throw new RuntimeException("(o゜▽゜)o☆ 비밀번호 틀렸쪄");
        }
        //UserDetails userDetails=new MyUserDetails(user.getUserId(),"ROLE_USER");
        MyUser myUser=MyUser.builder().userId(user.getUserId()).role("ROLE_USER"/*하드 코딩*/).build();

        //두가지 token에 myUser(유저 PK, 권한 정보를 담는다) -> why? : accessToken이 계속 백엔드로 요청을 보낼 때 Header에 넣어서 보내준다
        //refreshToken에 myUser담은 이유 : access토큰 만료 시 refresh 토큰 속 정보로 재발급(refresh 토큰 속 정보를 access 토큰에 넣음)
        //요청이 올 때마다 Request에 token이 담겨있는지 체크(filter에서 한다)
        //token에 담겨져 있는 myUser를 빼내서 사용하기 위해서 myUser를 담았다.
        /*카페에서 앱: 세션
         카페에서 쿠폰: 엑세스 토큰
         필요한 정보를 계속 들고 있지 않고 들고오게 하기 위함*/
        String accessToken= jwtTokenProvider.generateAccessToken(myUser);
        String refreshToken=jwtTokenProvider.generateRefreshToken(myUser);

        //refreshToken은 보안 쿠키를 이용해서 처리
        //refreshToken은 보안 쿠키를 이용해 처리(pront가 따로 작업을 하지 않아도 아래 cookie 값은 항상 넘어온다.)
        //쿠키는 항상 넘어오기 때문에 refreshToken이 필요 없어도 계속 보냄
        int refreshTokenMaxAge=appProperties.getJwt().getRefreshTokenCookieMaxAge();
        cookieUtils.deleteCookie(res, appProperties.getJwt().getRefreshTokenCookieName());
        cookieUtils.setCookie(res, appProperties.getJwt().getRefreshTokenCookieName(), refreshToken, refreshTokenMaxAge);


        return SignInRes.builder()
                .userId(user.getUserId())
                .nm(user.getNm())
                .pic(user.getPic())
                .accessToken(accessToken)
                .build();
    }

    public Map getAccessToken(HttpServletRequest req){
        Cookie cookie=cookieUtils.getCookie(req, appProperties.getJwt().getRefreshTokenCookieName());

        if(cookie==null){ //refresh 토큰 존재
            throw new RuntimeException();
        }
        String refreshToken/*MyUser를 가지고 있음*/=cookie.getValue();
        if(!jwtTokenProvider.isValidateToken(refreshToken)){ //시간이 만료되지 않음
            throw new RuntimeException();
        }
        //UserDetails auth=jwtTokenProvider.
        Authentication auth=jwtTokenProvider.getAuthentication(refreshToken);

        Map map=new HashMap(); //안 쓰는 걸 추천
        map.put("accessToken", "");
        return map;
    }

    public UserInfoGetRes getUserInfo(UserInfoGetReq p){
        return mapper.selProfileUserInfo(p);
    }

    @Transactional
    public String patchProfilePic(UserProfilePatchReq p){
        p.setSignedUserId(authenticationFacade.getLoginUserId());
        String fileNm=customFileUtils.makeRandomFileName(p.getPic());
        p.setPicName(fileNm);
        mapper.updProfilePic(p);

        //기존 폴더 삭제
        try {
            String midPath=String.format("user/%d", p.getSignedUserId());
            String delAbsoluteFolderPath = String.format("%s/user/%d", customFileUtils.uploadPath, p.getSignedUserId());
            customFileUtils.deleteFolder((delAbsoluteFolderPath));

            customFileUtils.makeFolders(midPath);
            String filePath = String.format("%s/%s", midPath, fileNm);
            customFileUtils.transferTo(p.getPic(), filePath); //메소드 이름 빨간줄=인자를 잘못 적었거나, 예외를 throw를 하고 있는지
            //try catch를 한 곳에 모아서 처리하는 것이 좋음
        }catch(Exception e){
            throw new RuntimeException(e);//원하는 메세지가 있다면 기입
        }
        return fileNm;//리턴은 문제가 없었을 때 문제가 없었을 때의 타입
    }
}

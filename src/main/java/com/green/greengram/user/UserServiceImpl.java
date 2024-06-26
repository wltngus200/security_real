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
    private final AuthenticationFacade authenticationFacade;
    private final AppProperties appProperties;

    public int postUser(MultipartFile mf, SignUpPostReq p){
        //암호화
        String hash=passwordEncoder.encode(p.getUpw());
        //String hash= BCrypt.hashpw(p.getUpw(),BCrypt.gensalt());
        p.setUpw(hash);
        String fileName= customFileUtils.makeRandomFileName(mf);
        p.setPic(fileName);

        int result=mapper.postUser(p);//PK값이 필요하기 때문에 먼저 insert

        //사진
        if(mf==null){return result;}
        String path=String.format("user/%s",p.getUserId());
        String target=String.format("%s/%s", path, fileName);
        try{
            customFileUtils.makeFolders(path);
            customFileUtils.transferTo(mf, target);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("사진 업로드 실패");
        }
        return result;
    }

    public SignInRes postSignIn(HttpServletResponse res, SignInPostReq p){
        User user=mapper.getUserId(p.getUid());
        if(user==null){throw new RuntimeException("(′д｀σ)σ 너는 누구야?");
        }else if(!BCrypt.checkpw(p.getUpw(),user.getUpw())){
            throw new RuntimeException("(o゜▽゜)o☆ 비밀번호 틀렸쪄");
        }
        //UserDetails userDetails=new MyUserDetails(user.getUserId(),"ROLE_USER");
        MyUser myUser=MyUser.builder().userId(user.getUserId()).role("ROLE_USER").build();

        String accessToken= jwtTokenProvider.generateAccessToken(myUser);
        String refreshToken=jwtTokenProvider.generateRefreshToken(myUser);


        //refreshToken은 보안 쿠키를 이용해서 처리
        int refreshTokenMaxAge=appProperties.getJwt().getRefreshTokenCookieMaxAge();
        cookieUtils.deleteCookie(res, "refresh-token");
        cookieUtils.setCookie(res, "refresh-token", refreshToken, refreshTokenMaxAge);


        return SignInRes.builder()
                .nm(user.getNm())
                .pic(user.getPic())
                .userId(user.getUserId())
                .build();
    }

    public Map getAccessToken(HttpServletRequest req){
        Cookie cookie=cookieUtils.getCookie(req, "refresh-token");


        if(cookie==null){ //refresh 토큰 존재
            throw new RuntimeException();
        }
        String refreshToken/*MyUser를 가지고 있음*/=cookie.getValue();
        if(!jwtTokenProvider.isValidateToken(refreshToken)){ //시간이 만료되지 않음
            throw new RuntimeException();
        }
        Authentication auth=jwtTokenProvider.getAuthentication(refreshToken);

        Map map=new HashMap();
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

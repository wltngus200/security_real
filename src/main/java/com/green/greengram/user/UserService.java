package com.green.greengram.user;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.user.model.*;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final CustomFileUtils customFileUtils;

    int postUser(MultipartFile mf, SignUpPostReq p){
        //암호화
        String hash= BCrypt.hashpw(p.getUpw(),BCrypt.gensalt());
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

    SignInRes postSignIn(SignInPostReq p){
        User user=mapper.getUserId(p.getUid());
        if(user==null){throw new RuntimeException("(′д｀σ)σ 너는 누구야?");
        }else if(!BCrypt.checkpw(p.getUpw(),user.getUpw())){
            throw new RuntimeException("(o゜▽゜)o☆ 비밀번호 틀렸쪄");
        }
        return SignInRes.builder()
                .nm(user.getNm())
                .pic(user.getPic())
                .userId(user.getUserId())
                .build();
    }
    public UserInfoGetRes getUserInfo(UserInfoGetReq p){
        return mapper.selProfileUserInfo(p);
    }
}

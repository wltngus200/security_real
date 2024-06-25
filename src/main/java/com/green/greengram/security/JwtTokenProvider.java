package com.green.greengram.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.model.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.config.validate.Validated;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.SignatureAttribute;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Slf4j
@Component //빈등록+싱글톤
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final ObjectMapper om;
    private final AppProperties appProperties;
    private SecretKey secretKey;

    @PostConstruct //시큐리티 에노테이션 //생성자 호출하고 1번 실행
    public void init(){
        secretKey= Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appProperties.getJwt().getSecret()));
    }

    public String generateAccessToken(UserDetails userDetails){
        return generateToken(userDetails, appProperties.getJwt().getAccessTokenExpiry());
    }
    public String generateRefreshToken(UserDetails userDetails){
        return generateToken(userDetails, appProperties.getJwt().getRefreshTokenExpiry());
    }
    private String generateToken(UserDetails userDetails, long tokenValidMilliSecond){
        return Jwts.builder()
                //헤더
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+tokenValidMilliSecond))
                //내용
                .claims(createClaims(userDetails))
                //서명
                .signWith(secretKey, Jwts.SIG.HS512) //서명처리(jwt 암호화 선택)
                .compact();//제일 마지막에 호출 되는 것이 리턴하는 타입 =String

    }

    private Claims createClaims(UserDetails userDetails){
        try{
            String json=om.writeValueAsString(userDetails);
            return Jwts.claims().add("signedUser",json).build();
                    //jwt의 구조 중 payload만 작성
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Claims getAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(secretKey) //똑같은 키로 복호화
                .build()
                .parseSignedClaims(token)
                .getPayload();//JWT안의 payload(지금은 claims)를 빼냄(리턴)
    }
    private UserDetails getUserDetailsFromToken(String token){
        try{
            Claims claims=getAllClaims(token); //jwt(인증 코드)에 저장된 claims를 얻어옴
            String json=(String)claims.get("signedUser"); //claims에 저장 되어있는 값을 얻어온다 (json)
            return om.readValue(json, MyUserDetails.class); //json -> 객체로 변화 (userDetails(myUserDetails))
            //jwt를 열어 myUserDetails를 보기 위한 메소드
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}

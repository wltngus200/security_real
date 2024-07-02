package com.green.greengram.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.model.AppProperties;
import com.green.greengram.security.MyUser;
import com.green.greengram.security.MyUserDetails;
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

/*
    V2와의 차이점은 SecretKey secretKey 멤버필드의 final 유무
    ObjectMapper, AppProperties를 생성자를 통해 DI 받음
    SecretKey는 생성자 호출 후에 @PostConstruct 애노테이션을 가지고 있는 init 메소드로 초기화
    init은 메소드(생성자 X) final 초기화 X => SecretKey를 final로 세팅 X
*/

@Slf4j
@Component //빈등록+싱글톤
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final ObjectMapper om;
    private final AppProperties appProperties;
    private SecretKey secretKey; /*V2와의 차이점 final의 유무*/

    @PostConstruct //시큐리티 에노테이션 //생성자 호출하고 1번 실행
    public void init(){
        secretKey= Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appProperties.getJwt().getSecret()));
    }

    public String generateAccessToken(MyUser myUser){
        return generateToken(myUser, appProperties.getJwt().getAccessTokenExpiry());
        //yaml 파일에서 app.jwt.access-token-expiry 내용을 가져오는 부분
    }
    public String generateRefreshToken(MyUser myUser, long tokenValidMilliSecond){
        return generateToken(myUser, appProperties.getJwt().getRefreshTokenExpiry());
        //yaml 파일에서 app.jwt.refresh-token-expiry 내용을 가져오는 부분
    }
    private String generateToken(MyUser myUser, long tokenValidMilliSecond){
        return Jwts.builder()
                //헤더 Header
                .issuedAt(new Date(System.currentTimeMillis())) //JWT 생성일시
                .expiration(new Date(System.currentTimeMillis()+tokenValidMilliSecond)) //JWT 만료일시
                //내용 Payload
                .claims(createClaims(myUser))
                //서명 Signature
                .signWith(secretKey, Jwts.SIG.HS512) //서명처리(jwt 암호화 선택, 위변조 검증)
                .compact();//제일 마지막에 호출 되는 것이 리턴하는 타입 =String //토큰생성

    }

    private Claims createClaims(MyUser myUser){
        try{
            String json=om.writeValueAsString(myUser);//객체 -> JSON
            return Jwts.claims().add("signedUser",json).build();
                    //jwt의 구조 중 payload만 작성
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Claims getClaims(String token){
        return Jwts
                .parser()
                .verifyWith(secretKey) //똑같은 키로 복호화
                .build()
                .parseSignedClaims(token)
                .getPayload();//JWT안의 payload(지금은 claims)를 빼냄(리턴)
    }
    public UserDetails getUserDetailsFromToken(String token){
        try{
            Claims claims=getClaims(token); //jwt(인증 코드)에 저장된 claims를 얻어옴
            String json=(String)claims.get("signedUser"); //claims에 저장 되어있는 값을 얻어온다 (json)
            MyUser myUser=om.readValue(json,MyUser.class); //Json>>객체 (UserDetails를 상속한 MyUserDetails)
            MyUserDetails myUserDetails= new MyUserDetails();
            myUserDetails.setMyUser(myUser);
            return myUserDetails;
            //jwt를 열어 myUserDetails를 보기 위한 메소드
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //SpringContextHolder에 저장할 자료 세팅(나중에 service에서 빼 쓸 값, 로그인 처리, 인가처리)
    public Authentication getAuthentication(String token){
        UserDetails userDetails=getUserDetailsFromToken(token); //MyUserDetails 객체 주소값
        return userDetails==null? null : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        //UserNamePasswordAuthenticationToken객체를 SpringContextHolder에 저장하는 자체만으로도 인증 완료
        //userDetails는 로그인한 사용자의 정보를 controller or service단에서 빼서 사용하기 위함
        //userDetails.getAuthorities()는 인가(권한)부분 세팅, 현재는 권한은 하나만 가질 수 있음, 다수 권한 가능
    }

    public boolean isValidateToken(String token) {
        try {
            //(original) 만료시간이 안 지났으면 리턴 false, 지났으면 리턴 true
            return !getClaims(token).getExpiration().before(new Date());
            //(변환) 만료시간이 안 지났으면 리턴 true, 지났으면 리턴 false

        } catch (Exception e) {
            return false;
        }
    }

    //요청이 오면 JWT를 열어보는 부분 > 헤더에서 토큰(JWT)을 꺼낸다.
    public String resolveToken(HttpServletRequest req) {
        //FE가 BE요청을 보낼 때 (로그인을 했다면)항상 JWT를 보낼건데 header에 서로 약속한 key에 저장해서 보낸다.
        String jwt = req.getHeader(appProperties.getJwt().getHeaderSchemaName());
        // String auth = req.getHeader("authorization"); 이렇게 작성한 것과 같음. key값은 변경가능
        if (jwt == null) { return null; }

        //위 if를 지나쳤다면 FE가 header에 authorization 키에 데이터를 담아서 보내왔다는 뜻.
        //auth에는 "Bearer JWT"문자열이 있을 것이다. 문자열이 'Bearer'로 시작하는지 체크

        // if(auth.startsWith("Bearer")) { //auth에 저장되어있는 문자열이 "Bearer"로 시작하면 true, 아니면 false
        // FE와 약속을 만들어야 함.
        // authorization: Bearer JWT문자열
        if(!jwt.startsWith(appProperties.getJwt().getTokenType())) {
            return null;
        }

        //순수한 JWT문자열만 뽑아내기 위한 문자열 자르기 + trim(양쪽 빈칸 제거)
        //return jwt.substring(appProperties.getJwt().getTokenType().length()).trim();
        return jwt.substring(appProperties.getJwt().getTokenType().length() + 1);
    }
}

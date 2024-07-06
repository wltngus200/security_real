//package com.green.greengram.security.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.green.greengram.common.model.AppProperties;
//import com.green.greengram.security.MyUser;
//import com.green.greengram.security.MyUserDetails;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.io.Decoders;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
////1. JWT 생성
////2. Request(요청)의 Header에서 Token값 얻기
////3. 확인(validate: 토큰 변질없나 만료시간 지났나?)
////4. Claim(data) 넣고 빼기
//
//
//@Slf4j
//@Component
//public class JwtTokenProviderV2 {
//    private final ObjectMapper om;
//    private final AppProperties appProperties;
//    private final SecretKey secretKey;
//
//    public JwtTokenProviderV2(ObjectMapper om, AppProperties appProperties){
//        this.om=om;
//        this.appProperties=appProperties;
//        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appProperties.getJwt().getSecret()));
//        //암호화, 복호화 할 때 사용하는 키를 생성하는 부분, decode 메소드에 보내는 아규먼트 값은 우리가 설정한 문자열
//    }
//
//    public String generateAccessToken(MyUser myUser){
//        return generateToken(myUser, appProperties.getJwt().getRefreshTokenExpiry());
//        //yaml파일에서 app.jwt.refresh-token-expiry 내용을 가져오는 부분
//    }
//
//    public String generateRefreshToken(MyUser myUser) {
//        return generateToken(myUser, appProperties.getJwt().getRefreshTokenExpiry());
//        //yaml파일에서 app.jwt.refresh-token-expiry 내용을 가져오는 부분
//    }
//
//    private String generateToken(MyUser myUser, long tokenValidMilliSecond){
//        return Jwts.builder()
//                .issuedAt(new Date(System.currentTimeMillis())) //jwt 생성일시
//                .expiration(new Date(System.currentTimeMillis()+tokenValidMilliSecond)) //jwt만료일시
//                .claims(createClaims(myUser)) //claims는 payload에 저장하고 싶은 내용 저장
//
//                .signWith(secretKey, Jwts.SIG.HS512) //서명(jwt암호화 선택, 위변조 검증)
//                .compact();//토큰 생성
//    }// 메소드 호출.메소드 호출.메소드 호출 >> 체이닝 기법(메소드 호출 시 자신의 주소값 리턴)
//
//    private Claims createClaims(MyUser myUser){
//        try{
//            String json = om.writeValueAsString(myUser); //객체 to json
//            return Jwts.claims().add("signedUser", json).build(); //claims에 json 저장
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public Claims/*내용(Payload)에 담기는 것*/ getAllClaims(String token){
//        return Jwts
//                .parser()
//                .verifyWith(secretKey/*객체 생성되며 final*/) //똑같은 키로 복호화
//                .build()
//                .parseSignedClaims(token)
//                .getPayload/*내용*/(); //jwt안에 들어있는 payload(claims)를 리턴
//    }
//
//    private UserDetails/*다형성*/ getUserDetailsFromToken(String token){
//        try{
//            Claims claims=getAllClaims(token);//jwt(인증코드)에 저장되어있는 Claims를 얻어온다.
//            String json=(String)claims.get("signedUser");///Claims에 저장되어 있는 값을 얻어옴 (JSON데이터)
//            MyUser myUser/*id, 역할*/=om.readValue(json, MyUser.class);//json -> 객체로 변화 (userDetails(myUserDetails))
//            MyUserDetails myUserDetails=new MyUserDetails();
//            myUserDetails.setMyUser(myUser);
//            return myUserDetails;
//        } catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    //SpringContextHolder에 저장할 자료를 세팅(나중에 Service단에서 빼서 쓸 값, 로그인 처리, 인가 처리를 위함
//    public Authentication/*인증*/ getAuthentication(String token) {
//        UserDetails userDetails = getUserDetailsFromToken(token); //MyUserDetails 객체 주소값 저장
//        return userDetails == null
//                ? null
//                //하나의 토큰에 저장 공간이 하나 할당 되고 securityContextHolder에(authentication) 저장
//                : new UsernamePasswordAuthenticationToken(userDetails,
//                null,
//                userDetails.getAuthorities());
//
//    }
//        //userNamePasswordAuthenticationToken 객체를 SpringContextHolder에 저장하는 자체만으로도 인증 완료
//        //userDetails는 로그인한 사용자의 정보를 controller or service단에서 빼서 사용하기 위함
//        //userDetails.getAuthorities()는 인가(권한)부분 세팅, 현재는 권한은 하나만 가질 수 있음, 다수 권한 가능
//
//
//    public boolean isValidateToken(String token){
//        try{ //기존 : 만료시간이 경과하지 않았으면 false 지났으면 true
//            return !getAllClaims(token).getExpiration().before(new Date()/*현재 일시로 만들어짐, 현재가 전이냐 후냐*/);
//            // 변환 : 만료시간이 안 지났으면 true, 지났으면 false
//        }catch(Exception e){
//            return false;
//        }
//    }
//    //요청이 오면 JWT를 열어보는 부분 >> 헤더에서 토큰(JWT)를 꺼낸다
//    public String resolveToken(HttpServletRequest req){ //요청이 오면 jwt를 열어보는 부분>>헤더에서 Jwt(토큰)을 꺼낸다
//        //front->back 로그인 요청을 보낼 때 항상 jwt를 보낼 건데 header에 저장해서 보낸다
//        String jwt=req.getHeader(appProperties.getJwt().getHeaderSchemaName());
//        //String jwt=req.getHeader("authorization");과 같다 프론트와의 약속(얼마든지 변경가능), key값은 변경가능
//        if (jwt==null){return null;}//통과했다=FE가 header에 authorization 키에 데이터를 담아서 보냈다
//                                    //auth에는 "Bearer JWT" 문자열이 존재, 문자열이 Bearer로 시작하는지 체크
//
//        //if(jwt.startsWith("Bearer")) //auth에 저장 된 문자열이 Bearer로 시작하면 true, 아니면 false -> 프론트와 협의
//        //authorization : Bearer JWT 문자열
//        if(!jwt.startsWith(appProperties.getJwt().getTokenType())){
//            //토큰에 저장된 문자열이 Bearer(jwt토큰이라는 의미)로 시작하면 True, 아니면 False
//            return null;
//        }
//
//        //순수한 JWT문자열만 뽑아내기 위한 문자열 자르기 + trim():양 쪽 빈칸 제거
//        return jwt.substring(appProperties.getJwt().getTokenType()/*yaml에 Bearer로 저장*/.length()).trim();
//    }
//}

//////////////////////////////////////////// 강사님  COPY ////////////////////////////////////////////

package com.green.greengram.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.common.model.AppProperties;
import com.green.greengram.security.MyUser;
import com.green.greengram.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
/*
  JWT 생성
, Request(요청)의 Header에서 token얻기
, 확인(validate:  토큰 변질이 없었나, 만료시간 지났나?)
, Claim(data) 넣고 빼기
*/

@Slf4j
@Component
public class JwtTokenProviderV2 {
    private final ObjectMapper om;
    private final AppProperties appProperties;
    private final SecretKey secretKey;

    public JwtTokenProviderV2(ObjectMapper om, AppProperties appProperties) {
        this.om = om;
        this.appProperties = appProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appProperties.getJwt().getSecret()));
        //암호화, 복호화할 때 사용하는 키를 생성하는 부분, decode메소드에 보내는 아규먼트값은 우리가 설정한 문자열
    }

    public String generateAccessToken(MyUser myUser) {
        return generateToken(myUser, appProperties.getJwt().getAccessTokenExpiry());
        //yaml파일에서 app.jwt.access-token-expiry 내용을 가져오는 부분
    }

    public String generateRefreshToken(MyUser myUser) {
        return generateToken(myUser, appProperties.getJwt().getRefreshTokenExpiry());
        //yaml파일에서 app.jwt.refresh-token-expiry 내용을 가져오는 부분
    }

    private String generateToken(MyUser myUser, long tokenValidMilliSecond) {
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis())) // JWT 생성일시
                .expiration(new Date(System.currentTimeMillis() + tokenValidMilliSecond)) // JWT 만료일시
                .claims(createClaims(myUser)) // claims는 payload에 저장하고 싶은 내용을 저장

                .signWith(secretKey, Jwts.SIG.HS512) // 서명 (JWT 암호화 선택, 위변조 검증)
                .compact(); //토큰 생성

        //  .메소드호출.메소드호출.메소드호출   >>  체이닝 기법, 원리는 메소드호출 시 자신의 주소값 리턴을 하기 때문
    }

    private Claims createClaims(MyUser myUser) {
        try {
            String json = om.writeValueAsString(myUser); // 객체 to JSON
            return Jwts.claims().add("signedUser", json).build(); // Claims에 JSON 저장
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(secretKey) //똑같은 키로 복호화
                .build()
                .parseSignedClaims(token)
                .getPayload(); // JWT 안에 들어있는 payload (Claims)를 리턴
    }

    public UserDetails getUserDetailsFromToken(String token) {
        try {
            Claims claims = getClaims(token); //JWT(인증코드)에 저장되어 있는 Claims를 얻어온다.
            String json = (String)claims.get("signedUser"); //Claims에 저장되어 있는 값을 얻어온다. (그것이 JSON(데이터))
            MyUser myUser = om.readValue(json, MyUser.class); //JSON > 객체로 변환 (그것이 UserDetails, 정확히는 MyUserDetails)
            MyUserDetails myUserDetails = new MyUserDetails();
            myUserDetails.setMyUser(myUser);
            return myUserDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //SpringContextHolder에 저장할 자료를 세팅(나중에 Service단에서 빼서 쓸 값, 로그인 처리, 인가 처리를 위해)
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token); //MyUserDetails 객체주소값
        return userDetails == null
                ? null
                : new UsernamePasswordAuthenticationToken(userDetails
                , null
                , userDetails.getAuthorities()
        );

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
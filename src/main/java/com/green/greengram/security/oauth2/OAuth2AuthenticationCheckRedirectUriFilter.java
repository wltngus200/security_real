package com.green.greengram.security.oauth2;

import com.green.greengram.common.model.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationCheckRedirectUriFilter extends OncePerRequestFilter {//필터를 상속 받은 것만 필터에 들어갈 수 있음
    private final AppProperties appProperties; //우리가 허용할 redirect값을 yaml에 저장 했고 극 값을 받아오는 곳이기 때문

    @Override
    protected void doFilterInternal(HttpServletRequest request, //요청에 대한 모든 정보
                                    HttpServletResponse response, //응답할 수 있는 객체
                                    FilterChain filterChain) //다음 필터로 req, res 전달시 사용
                                    throws ServletException, IOException {
        String requestUri=request.getRequestURI(); //host값 제외한 uri를 리턴
                                        //ex. http://localhost:8080/aaa/bbb-> /aaa/bbb/를 리턴

        log.info("requestUri : {}", requestUri);

        //주소값이 /oauth2(security configuration 에서 세팅한 값-baseUri)으로 시작하는지 체크
        if(requestUri.startsWith(appProperties.getOauth2().getBaseUri())){
            String redirectUriParam=request.getParameter("redirect_uri");// ???? value값 받아오기
            //여기 담긴 값과 yaml에 저장된 리스트 중 일치하는 게 있는지 successhandler에서 has~~
            if(redirectUriParam!=null && !hasAuthorizedRedirectUri(redirectUriParam)){ //허용하지 않은 uri라면
                String errRedirectUrl= UriComponentsBuilder.fromUriString("/err_message")
                        .queryParam("message", "유효한 Redirect URL이 아닙니다.").encode()
                        .toUriString();
                //"/err_massage?message=유효한 Redirect URL이 아닙니다"
                response.sendRedirect(errRedirectUrl);
                return;
            }

        }filterChain.doFilter(request, response);

    }
    private boolean hasAuthorizedRedirectUri (String uri){
        //주소값과 관련된 메소드 지원 ex.쿼리스트링 값 빼오기
        URI clientRedirectUri/*쿠키에 저장됨*/ = URI.create(uri);
        log.info("clientRedirectUri.getHost():{}", clientRedirectUri.getHost());
        //http://localhost:8080/ 까지만 나온다
        log.info("clientRedirectUri.getPort():{}", clientRedirectUri.getPort());
        log.info("clientRedirectUri.getPath():{}", clientRedirectUri.getPath());


        for (String redirectUri/*yaml에 저장*/: appProperties.getOauth2().getAuthorizedRedirectUris()) {
            URI authorizedUri = URI.create(redirectUri); //하나씩 넘어옴
            if (clientRedirectUri.getHost().equalsIgnoreCase(authorizedUri.getHost())
                    && /*port num = int*/clientRedirectUri.getPort() == authorizedUri.getPort()
                    && clientRedirectUri.getPath().equals(authorizedUri.getPath()))
                 {
                return true; //이후에 더 있더라도 같으면 종료
            }
        }
        return false;
    }


}

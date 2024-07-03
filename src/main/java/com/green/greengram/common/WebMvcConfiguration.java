package com.green.greengram.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final String uploadPath;

    @Override
    //브라우저의 보안을 켠다
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true) // 쿠키 요청을 허용
                .maxAge(3600);
    }

    public WebMvcConfiguration(@Value("${file.directory}")String uploadPath){
        this.uploadPath=uploadPath;
    }
    //Value
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/pic/**")
                //주소값이 이렇게 들어오면(~~라고 요청이 들어온다면) 아래쪽 uploadPath로 매핑
                //pic/user/4/(파일이름)
                .addResourceLocations("file:"+uploadPath);
        registry.addResourceHandler("/**") //모든 요청 (픽에서도 컨트롤러에서도 안 걸린 모든 요청)
                .addResourceLocations("classpath:/static/**") //url과 같은 이름의 파일이 필요한데 우린 없다
                .resourceChain(true)
                .addResolver(new PathResourceResolver(){
                    @Override //리소스=파일                     //feed   //"classpath:/static/**"
                    protected Resource getResource(String resourcePath, Resource location) throws IOException{
                        Resource requestedResource= location.createRelative(resourcePath);

                        if(requestedResource.exists()&&requestedResource.isReadable()){
                            return requestedResource;
                        }return new ClassPathResource("/static/index.html");//진짜 존재하는 요청이 아니면 인덱스 파일을 써라
                    }
        });
    }
}

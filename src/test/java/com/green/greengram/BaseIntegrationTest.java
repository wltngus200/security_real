package com.green.greengram;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Import({CharEncodingConfiguration.class})//한글 깨지지 않게
@ActiveProfiles("tdd")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT/*포트를 바꿔가며 테스트*/) //통합테스트 어노테이션 서비스, 컨트롤러, 매퍼 모두 객체화한다
@AutoConfigureMockMvc // 컨트롤러 테스트에서 했던 설정 (MockMvc)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //매퍼테스트에서 했던 설정
//TDD는 인메모리데이터베이스인 H2로 하는 게 보통인데 그걸로 대체 하지 않겠다는 세팅
@Transactional //통합 테스트 후 롤백
public class BaseIntegrationTest { //프로텍티드로 하는 이유: 조금 있다 상속 받기 때문 추상화 해놓고 상속, 프라이빗이면 자식객체도 접근 불가
    //프로텍티드는 패키지가 달라져도 상속 관계면 접근이 가능
    @Autowired protected MockMvc mvc; //통신을 담당(=postman) 브라우저로 네이버 들어가듯 요청(request)을 보내보는 것
    @Autowired protected ObjectMapper om; //직렬화와 역직렬화를 담당 String(Json)을 객체로 객체를 String으로 바꾸는 잭슨라이브러리

    //private(클래스의 중괄호를 벗어나는 순간 사용 불가) default(거의 안 씀, 같은 패키지 안에서는 접근 가능)
    //protected(상속 관계면 접근 가능) public(완전 공개, 누구나 접근 가능)


}

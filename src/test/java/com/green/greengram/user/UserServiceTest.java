package com.green.greengram.user;

import com.green.greengram.user.model.SignUpPostReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@Import({UserServiceImpl.class})
class UserServiceTest {
    @MockBean
    private UserMapper mapper;

    @Autowired
    private UserService service;

    @Test
    void postUser() {
        SignUpPostReq req=new SignUpPostReq();
    }

    @Test
    void postSignIn() {
    }

    @Test
    void getUserInfo() {
    }

    @Test
    void patchProfilePic() {
    }
}
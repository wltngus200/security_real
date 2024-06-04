package com.green.greengram.user;

import com.green.greengram.common.CustomFileUtils;
import com.green.greengram.user.model.UserProfilePatchReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import({UserServiceImpl.class})
@TestPropertySource(
        properties = {
                "file.directory=D:/2024_BACK_JI/download/greengram_tdd/"
        }
)
public class UserService2Test {
    @Value("${file.directory}")
    String uploadPath;
    @MockBean //호출이 되었는지 아닌지 알 수 있다
    private UserMapper mapper;
    @MockBean
    private CustomFileUtils utils;

    @Autowired
    private UserService service;

    @Test
    void patchProfilePic() throws Exception {//진짜로 파일이 옮겨지거나 하지는 않음 //코드만 체크
        //임무를 주지 않으면 가짜는 null을 리턴
        long signedUserId1 = 500;
        UserProfilePatchReq p1 = new UserProfilePatchReq();//호출을 위한 파라미터 세팅
        p1.setSignedUserId(signedUserId1);

        MultipartFile fm1 = new MockMultipartFile("pic", "b.jpg", "image/jpg",
                new FileInputStream(String.format("%stest/b.jpg", uploadPath)));
        p1.setPic(fm1);

        //위에까진 가짜 파라미터를 만드는 작업
        String checkFileNm1="a1b2.jpg"; //다른 값도 넣어서 확인
        given(utils.makeRandomFileName(fm1)).willReturn(checkFileNm1);
        String fileNm1=service.patchProfilePic(p1);
        assertEquals(checkFileNm1, fileNm1); //같은 이름인지 확인

        verify(mapper).updProfilePic(p1);
        String midPath=String.format("user/%d", p1.getSignedUserId());
        String delAbsoluteFolderPath = String.format("%s/%s", utils.uploadPath, midPath);
        verify(utils).deleteFolder(delAbsoluteFolderPath);
        verify(utils).makeFolders(midPath);
        String target=String.format("%s/%s", midPath, fileNm1);
        verify(utils).transferTo(p1.getPic(), target); //메소드가 필요로 하는 파라미터 확인~

    }
}

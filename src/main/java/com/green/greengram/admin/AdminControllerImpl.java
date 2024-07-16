package com.green.greengram.admin;

import com.green.greengram.admin.model.GetProviderRes;
import com.green.greengram.common.model.MyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminControllerImpl {
    private AdminServiceImpl service;
    @GetMapping("provider-count")
    public MyResponse<List<GetProviderRes>> getAdmin(){
        List<GetProviderRes> result=service.getAdmin();
        return MyResponse.<List<GetProviderRes>>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg(HttpStatus.OK.toString())
                .resultData(result)
                .build();
    }

}

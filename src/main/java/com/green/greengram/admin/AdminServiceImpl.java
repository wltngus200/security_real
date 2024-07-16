package com.green.greengram.admin;

import com.green.greengram.admin.model.GetProviderRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl {
    private AdminMapper mapper;
    public List<GetProviderRes> getAdmin(){
        return mapper.getAdmin();
    }
}

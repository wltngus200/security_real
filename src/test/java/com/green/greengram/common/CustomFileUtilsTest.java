package com.green.greengram.common;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CustomFileUtilsTest {

    @Test
    void deleteFolder() {
        CustomFileUtils customFileUtils=new CustomFileUtils("");
        String delFolderPath="D:\\2024_BACK_JI\\download\\delFolder2";
        File delFolder=new File(delFolderPath);
        delFolder.delete();
    }
    @Test
    void deleteFolder2() {
        CustomFileUtils customFileUtils=new CustomFileUtils("");
        String delFolderPath="D:\\2024_BACK_JI\\download\\delFolder";
        File delFolder=new File(delFolderPath);
        delFolder.delete();//하위 폴더, 파일이 있어 삭제가 되지 않음
    }
    @Test
    void deleteFolder3() {
        CustomFileUtils customFileUtils=new CustomFileUtils("");
        String delFolderPath="D:\\2024_BACK_JI\\download\\delFolder";
        File delFolder=new File(delFolderPath);
    }
}
package com.green.greengram.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Component //빈등록
@Getter
public class CustomFileUtils {
    /*
    @Value("${file.dir}")//dir은 디렉토리 yaml에 명시된 경로에 파일을 담겠다 네이밍 맞추기
    private String uploadPath; //여기에 =주소값 하려면 \\로 슬래시 넣기(이스케이프 문자)
    //내용이 자바안에 없다. 장점: 값을 바꿨을 때 컴파일이 필요 없다(값을 참조할 것이기 때문)
    //객체화가 되고 나서야 값을 넣을 수 있기 때문에 final은 붙일 수 없다
    */

    public final String uploadPath; //value에노테이션을 통해 값이 들어감
    public CustomFileUtils(@Value("${file.directory}")String uploadPath){
        //yaml과 분류 명, 파일 이름이 일치
        //빈등록을 해야되는데 클래스를 객체화 하려고 하면 파라미터로 뭘 넣을 지 모름
        //그걸 알려주는 게 Value
        /*value : yaml파일(세팅) 사용할 라이브러리 값+내가 사용할 값(커스텀할 속성) 여기 저장된 값 DI가 되어 파라미터로 들어옴
         */
        this.uploadPath=uploadPath;//직접 안 적고 이렇게 하는 이유
        //자바파일은 클래스 파일로 바뀌어야 함 고급언어(사람언어)--컴파일-->저급언어(컴퓨터언어) value로 참조하면 컴파일 필요성 감소
    }
    public String makeFolders(String path){//path경로
        File folder= new File(uploadPath, path);//디렉토리도 파일도 파일 /는 알아서 넣어줌
        //이 경로를 가진 파일 ?
        folder.mkdirs();
        //mkdirs는 경로명 yaml경로에 따라 큰 폴더를 만들고 그 안에 폴더 또 생성
        //mkdir는 경로명이 정확하게 일치해야만 가능 (리턴타입 boolean)
        return folder.getAbsolutePath(); //절대 주소값(처음부터 끝까지 모든 경로 저장)<->상대경로
        //폴더 만들기 오른쪽 폴더 제너레이트 테스트->커스텀파일 유틸테스트 클래스가 생김
    }
//파일 이름이 여러가지 언어가 섞여 있어 에러를 유발 할 수 있기 때문에 영어와 숫자로 변경
    //UUID랜덤 파일 명(JAVA 내장)

    //파일 명에서 확장자 얻어오기
    public String getExt(String fileName){//d2.dt3ra.jpg
        int idx=fileName.lastIndexOf(".");//내가 찾는 문자열이 있다면 양수값 없으면 음수값
        //indexOf는 왼쪽에서부터 찾음 <-> lastIndexOf 오른쪽부터 찾음
        return fileName.substring(idx);
    }
    //오버로딩
    public String makeRandomFileName(){
        return UUID.randomUUID().toString();
    }

    //랜덤 파일 명 with 확장자 만들기
    public String makeRandomFileName(String fileName){//파라미터로 원본의 파일명 날아옴
        return makeRandomFileName()+getExt(fileName);
    }

    public String makeRandomFileName(MultipartFile mf){
        return mf==null || mf.isEmpty() ? null : makeRandomFileName(mf.getOriginalFilename());
    }//mf.isEmpty() 형식은 넣었는데 비었다(포스트맨에 체크는 되어있는데 비어있다) / null은 형식도 없다(PostMan체크 없음)

    //파일저장(target 경로/파일명)
    public void transferTo(MultipartFile mf, String target) throws Exception { //문제가 발생하면 내가 해결 안 하고 호출한 자리에서 해결해
        //클라이언트가 서버에 파일을 보내면
        File saveFile=new File(uploadPath, target); //최종경로+파일이름 //바꿔주는 작업
        mf.transferTo(saveFile);//에러를 감싸주어야함 //transfer되어라
        //파일 객체는 전체 경로, 확장자
        //mf에게 어디에 저장할 지 알려줘야됨
    }

    //폴더 삭제= 폴더 안에 또 파일이나 폴더가 있으면 삭제 할 수 없다 +내가 폴더를 켜놓고 있어도 지울 수 없다-> 재귀호출로 해결
    public void deleteFolder(String fullPath){//folderPath는 상대주소 ex. " /user/3 "
        File folder = new File(fullPath); //폴더=디렉토리
        if(folder.exists()&& folder.isDirectory()){//폴더가 존재하고, 디렉토리라면 작업 (확장자가 없다=폴더)
            File[] files=folder.listFiles();
            //파일 객체로 객체화 해서 배열 형태로 리턴(여러개 일 수 있기 떄문)

            for(File f: files){
                if(f.isDirectory()){//폴더인가 TF
                    deleteFolder(f.getAbsolutePath());//재귀호출=자기 자신한테 일을 시킴(깊이를 알 수 없을 떄 좋음)
                    //제일 먼저 호출 된 게 제일 마지막에 끝남 호출 된 애의 업무가 끝나야 호출 한 애가 일을 시작=스택(푸쉬+팝)
                    //파일도 있고 폴더더 있는게 리스트 파일즈(files)로 들어옴
                    //폴더인 걸 확인했으면 deleteFolder를 호출
                }else {
                    f.delete();
                }//끝나면 빈 상태가 됨
                folder.delete();

                }
            }
    }
}

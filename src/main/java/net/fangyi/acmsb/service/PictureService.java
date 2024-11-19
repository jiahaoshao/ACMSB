package net.fangyi.acmsb.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PictureService {

    //保存头像
    void updateImg(MultipartFile multipartFile) throws IOException;

    byte[] getImg(int pid);
}

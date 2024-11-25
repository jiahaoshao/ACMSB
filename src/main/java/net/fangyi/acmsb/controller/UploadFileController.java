package net.fangyi.acmsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import net.fangyi.acmsb.Util.UploadFileUtil;
import net.fangyi.acmsb.repository.UserRepository;
import net.fangyi.acmsb.result.Result;
import net.fangyi.acmsb.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
@CrossOrigin //解决跨域问题
public class UploadFileController {
    private static final Logger logger = LoggerFactory.getLogger(UploadFileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public static final int AVATAR_MAX_SIZE = 5 * 1024 * 1024;
    /* 限制上传文件的类型 */
    public static final List<String> AVATAR_TYPE = new ArrayList<>();
    static { // 初始化
        AVATAR_TYPE.add("image/jpeg");
        AVATAR_TYPE.add("image/png");
        AVATAR_TYPE.add("image/bmp");
        AVATAR_TYPE.add("image/gif");
    }

    @Operation(summary = "上传头像到本地")
    @PostMapping("/avatar")
    public ResponseEntity<?> upload_avatar(@RequestParam("file") MultipartFile file, @RequestParam("uid") int uid) throws IOException {
        if(userRepository.findById(uid).isEmpty()) {
            return ResponseEntity.ok(Result.error("用户不存在"));
        }
        if(file.isEmpty()){
            return ResponseEntity.ok(Result.error("文件为空"));
        }
        if(file.getSize() > AVATAR_MAX_SIZE){
            return ResponseEntity.ok(Result.error("文件大小超出限制"));
        }
        String contentType = file.getContentType();
        if(!AVATAR_TYPE.contains(contentType)){
            return ResponseEntity.ok(Result.error("文件类型不支持"));
        }
        String avatarUrl = UploadFileUtil.upload(file, "static/images/avatar");
        if(avatarUrl == null){
            return ResponseEntity.ok(Result.error("上传失败"));
        }
        userService.UpdateAvatar(uid, avatarUrl);
        return ResponseEntity.ok(Result.success("上传成功"));
    }
    
}

package net.fangyi.acmsb.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import net.fangyi.acmsb.Util.UploadFileUtil;
import net.fangyi.acmsb.Util.UploadGiteeImgBed;
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
import java.util.*;

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
        if(userRepository.findByUid(uid) == null){
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

    @Operation(summary = "上传图片到本地")
    @PostMapping("/article_images")
    public ResponseEntity<?> uploadArticleImages(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return ResponseEntity.ok(Result.error("文件为空"));
            }
            if (file.getSize() > AVATAR_MAX_SIZE) {
                return ResponseEntity.ok(Result.error("文件大小超出限制"));
            }
            String contentType = file.getContentType();
            if (!AVATAR_TYPE.contains(contentType)) {
                return ResponseEntity.ok(Result.error("文件类型不支持"));
            }
            String articleImageUrl = UploadFileUtil.upload(file, "static/images/article_image");
            if (articleImageUrl == null) {
                return ResponseEntity.ok(Result.error("上传失败"));
            }
            uploadedUrls.add(articleImageUrl);
        }
        return ResponseEntity.ok(Result.success("上传成功", uploadedUrls));
    }

    /**
     *  上传图片
     * @param multipartFile 文件对象
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadImg")
    public ResponseEntity<?> uploadImg(@RequestParam("file")MultipartFile multipartFile) throws IOException {
        logger.info("uploadImg()请求已来临...");
        //根据文件名生成指定的请求url
        String originalFilename = multipartFile.getOriginalFilename();
        if(originalFilename == null){
            return ResponseEntity.ok(Result.error("上传失败"));
        }
        String targetURL = UploadGiteeImgBed.createUploadFileUrl(originalFilename);
        logger.info("目标url：{}", targetURL);
        //请求体封装
        Map<String, Object> uploadBodyMap = UploadGiteeImgBed.getUploadBodyMap(multipartFile.getBytes());
        //借助HttpUtil工具类发送POST请求
        String JSONResult = HttpUtil.post(targetURL, uploadBodyMap);
        //解析响应JSON字符串
        JSONObject jsonObj = JSONUtil.parseObj(JSONResult);
        //请求失败
        if(jsonObj == null || jsonObj.getObj("commit") == null){
            return ResponseEntity.ok(Result.error("上传失败"));
        }
        //请求成功：返回下载地址
        JSONObject content = JSONUtil.parseObj(jsonObj.getObj("content"));
        logger.info("响应data为：{}", content.getObj("download_url"));
        return  ResponseEntity.ok(Result.success("上传成功", content.getObj("download_url")));
    }

}

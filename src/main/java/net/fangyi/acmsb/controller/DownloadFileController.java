package net.fangyi.acmsb.controller;


import net.fangyi.acmsb.Util.Base64Util;
import net.fangyi.acmsb.Util.UploadFileUtil;
import net.fangyi.acmsb.service.PictureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/download")
@CrossOrigin
public class DownloadFileController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadFileController.class);

    @Autowired
    private PictureService pictureService;

    @GetMapping("/avatar")
    public byte[] download_avatar(@RequestParam int uid) throws IOException {
        logger.info("download_avatar, uid: {}", uid);
        byte[] b = pictureService.getImg(uid);
        logger.info("download_avatar, pic_data: {}", b);
        return b;
    }

}

package net.fangyi.acmsb.controller;


import net.fangyi.acmsb.Util.Base64Util;
import net.fangyi.acmsb.Util.UploadFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/download")
@CrossOrigin
public class DownloadFileController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadFileController.class);

    @GetMapping("/getimagebase64")
    public ResponseEntity<?> getImageBase64(@RequestParam("filePath") String filePath) {
        logger.info("Get image base64: {}", filePath);
        String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";

        File file = new File(resourcesPath +filePath);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("File not found");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            String base64 = Base64Util.encodeToStr(bytes);
            return ResponseEntity.ok(base64);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading file");
        }
    }
}

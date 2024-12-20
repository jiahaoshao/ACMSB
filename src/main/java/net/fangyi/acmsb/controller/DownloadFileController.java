package net.fangyi.acmsb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/download")
@CrossOrigin
@Tag(name = "文件下载控制器", description = "用于处理文件下载的接口")
public class DownloadFileController {
    private static final Logger logger = LoggerFactory.getLogger(DownloadFileController.class);

    @Operation(summary = "获取图片的Base64编码", description = "根据文件路径获取图片的Base64编码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取Base64编码",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "文件未找到"),
            @ApiResponse(responseCode = "500", description = "读取文件时发生错误")
    })
    @GetMapping("/getimagebase64")
    public ResponseEntity<?> getImageBase64(@Parameter(description = "文件路径") @RequestParam("filePath") String filePath) {
        logger.info("Get image base64: {}", filePath);
        String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";

        File file = new File(resourcesPath + filePath);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("File not found");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return ResponseEntity.ok(base64);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading file");
        }
    }
}
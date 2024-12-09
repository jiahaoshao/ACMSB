package net.fangyi.acmsb.controller;

import io.swagger.annotations.ApiResponse;
import net.fangyi.acmsb.Util.Base64Util;
import net.fangyi.acmsb.Util.FileUtils;
import net.fangyi.acmsb.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/github")
public class GithubController {
    private static final Logger logger = LoggerFactory.getLogger(GithubController.class);

    @Value("${Github.branch}")
    private String branch;  // Github 仓库的分支

    @Value("${Github.name}")
    private String committerName;  // 提交者的姓名

    @Value("${Github.email}")
    private String committerEmail;  // 提交者的邮箱

    @Value("${Github.message}")
    private String commitMessage;  // 提交的消息

    @Value("${Github.OWNER}")
    private String owner;  // Github 仓库的所有者

    @Value("${Github.REPO}")
    private String repo;  // Github 仓库的名称

    @Value("${Github.Authorization}")
    private String token;  // Github 个人访问令牌
    
    
    
    
    
    /**
     * 上传文件到Github仓库
     *
     * @param filePath 文件在仓库中的路径
     * @param file  要上传的文件
     * @return ResponseEntity<?> 包含上传结果信息的响应
     * @throws IOException IO异常
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("filepath") String filePath) throws IOException, InterruptedException {
        //String path = URLEncoder.encode(filePath, StandardCharsets.UTF_8);

        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null){
            return ResponseEntity.ok(Result.error("文件上传失败"));
        }
        //获取文件后缀
        String suffix = FileUtils.getFileSuffix(originalFilename);//使用到了自己编写的FileUtils工具类
        //拼接存储的图片名称
        String filename = System.currentTimeMillis()+"_"+ UUID.randomUUID().toString()+suffix;

        String path = filePath + filename;

        logger.info("Uploading file to Github repository: {}", path);

        String contents = Base64.getEncoder().encodeToString(file.getBytes());

        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
        logger.info("API URL: {}", apiUrl);

        String basicAuth = Base64.getEncoder().encodeToString((token + ":").getBytes());
        String authorizationHeader = "Basic " + basicAuth;

        String requestBody = String.format(
                "{\"message\":\"%s\", \"branch\":\"%s\", \"committer\":{\"name\":\"%s\", \"email\":\"%s\"}, \"content\":\"%s\"}",
                commitMessage, branch, committerName, committerEmail, contents
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", authorizationHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github.v3+json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            logger.info("Response Code: {}", response.statusCode());
            logger.info("Response Body: {}", response.body());

            String downloadUrl = "https://jsd.cdn.zzko.cn/gh/" + owner + "/" + repo + "/" + path;
            return ResponseEntity.ok(Result.success("上传成功", downloadUrl));
        } else {
            logger.error("File upload failed: {}", response.body());
            return ResponseEntity.ok(Result.error("上传失败", response.body()));
        }
    }

    @GetMapping("/download_base64")
    public ResponseEntity<?> download(@RequestParam String fileUrl) throws IOException {
        String base64 = ImageBase64(fileUrl);

        return ResponseEntity.ok(base64);
    }

    public String convertFileToBase64(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();
        String mimeType = connection.getContentType();
        try (InputStream inputStream = url.openStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[10240];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] fileBytes = byteArrayOutputStream.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            base64 = "data:" + mimeType + ";base64," + base64;
            return base64;
        }
    }

    /**
     *
     * 远程读取image转换为Base64字符串
     * @param imgUrl
     * @return
     */
    private String ImageBase64(String imgUrl) {
        final String[] base64Result = {imgUrl};
        Thread thread = new Thread(() -> {
            URL url = null;
            InputStream is = null;
            ByteArrayOutputStream outStream = null;
            HttpURLConnection httpUrl = null;
            try {
                url = new URL(imgUrl);
                URLConnection connection = url.openConnection();
                String mimeType = connection.getContentType();
                httpUrl = (HttpURLConnection) url.openConnection();
                httpUrl.connect();
                httpUrl.getInputStream();
                is = httpUrl.getInputStream();

                outStream = new ByteArrayOutputStream();
                //创建一个Buffer字符串
                byte[] buffer = new byte[1024];
                //每次读取的字符串长度，如果为-1，代表全部读取完毕
                int len = 0;
                //使用一个输入流从buffer里把数据读取出来
                while ((len = is.read(buffer)) != -1) {
                    //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                    outStream.write(buffer, 0, len);
                }
                // 对字节数组Base64编码
                String base64 = Base64Util.encodeToStr(outStream.toByteArray());
                base64 = "data:" + mimeType + ";base64," + base64;
                base64Result[0] = base64;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (httpUrl != null) {
                    httpUrl.disconnect();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return base64Result[0];
    }

    /**
     * 解析Github API响应中的 download_url
     *
     * @param jsonResponse Github API的JSON响应
     * @return download_url
     */
    private static String parseDownloadUrl(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("\"download_url\":\"") + 16;
        int endIndex = jsonResponse.indexOf("\"", startIndex);
        return jsonResponse.substring(startIndex, endIndex);
    }

    /**
     * 将内容进行Base64编码
     *
     * @param content 原始内容
     * @return Base64编码后的内容
     */
    private String encryptToBase64(String content) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(content)));
    }

    public String uploadAvatar(MultipartFile file, String branch, String committerName, String committerEmail, String commitMessage, String owner, String repo, String token) throws IOException, InterruptedException {

        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null){
            return "上传失败";
        }
        //获取文件后缀
        String suffix = FileUtils.getFileSuffix(originalFilename);//使用到了自己编写的FileUtils工具类
        //拼接存储的图片名称
        String filename = System.currentTimeMillis()+"_"+ UUID.randomUUID().toString()+suffix;

        String filePath = "images/avatar/";

        String path = filePath + filename;

        logger.info("Uploading file to Github repository: {}", path);

        String contents = Base64.getEncoder().encodeToString(file.getBytes());

        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
        logger.info("API URL: {}", apiUrl);

        String basicAuth = Base64.getEncoder().encodeToString((token + ":").getBytes());
        String authorizationHeader = "Basic " + basicAuth;

        String requestBody = String.format(
                "{\"message\":\"%s\", \"branch\":\"%s\", \"committer\":{\"name\":\"%s\", \"email\":\"%s\"}, \"content\":\"%s\"}",
                commitMessage, branch, committerName, committerEmail, contents
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", authorizationHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github.v3+json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            logger.info("Response Code: {}", response.statusCode());
            logger.info("Response Body: {}", response.body());

            String downloadUrl = "https://jsd.cdn.zzko.cn/gh/" + owner + "/" + repo + "/" + path;
            return downloadUrl;
        } else {
            logger.error("File upload failed: {}", response.body());
            return "上传失败";
        }
    }
}


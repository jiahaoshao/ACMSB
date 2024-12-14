package net.fangyi.acmsb.controller;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import io.swagger.annotations.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.fangyi.acmsb.entity.ChatRequest;
import net.fangyi.acmsb.entity.ChatRequestCopy;
import net.fangyi.acmsb.entity.Message;
import net.fangyi.acmsb.repository.ChatRequestCopyRepository;
import net.fangyi.acmsb.repository.UserRepository;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.apache.catalina.connector.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "控制器：AI控制器", description = "描述：测试AI接口")
@RestController
@RequestMapping("/ai")
@CrossOrigin //解决跨域问题
public class AiController {
    private static final Logger logger = LoggerFactory.getLogger(AiController.class);
    // .....

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRequestCopyRepository chatRequestCopyRepository;

    //    private static final String CLIENT_ID = "RNLjFTsINgFUlLhiQotE1X03"; // Replace with your API Key
//    private static final String CLIENT_SECRET = "t12jZNww9mE6OQMZQFOC0ZItw5gwpZX4"; // Replace with your Secret Key
    private static final String CLIENT_ID = "5523gDFbQdchEDikISN68Zyd"; // Replace with your API Key
    private static final String CLIENT_SECRET = "uhR7xjRlycTTL2sXPTMVENCTb110IRFN"; // Replace with your Secret Key
    private static final String ACCESS_URL = "https://aip.baidubce.com/oauth/2.0/token";

    private static final String API_KEY = "xai-6mIqkZJWw3A5WawUuTz5tQ1dcx08IoGQfT09MDJNnJ17bjxX2vbZF0HZtWQ428z1FbzOGNLLzcczTQF5";
    //private static final String API_KEY = "xai-Qc3Vo5Pt2GQCHTpTlEIfXOia3i4RwToNAWGQB305DIuFzhS0QbSIvV6u2rIA3Ma5QVyevyPjFkeIEQ8H";
    private static final String CHAT_URL = "https://api.x.ai/v1/chat/completions";
    private static final ObjectMapper objectMapper = new ObjectMapper();



    /**
     * 聊天AI
     * @param request 消息列表
     * @return AI的回复
     */
    @Operation(summary = "测试AI聊天的注解方法Post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "请求成功"),
            @ApiResponse(responseCode = "400", description = "请求参数没填好"),
            @ApiResponse(responseCode = "401", description = "没有权限"),
            @ApiResponse(responseCode = "403", description = "禁止访问"),
            @ApiResponse(responseCode = "404", description = "请求路径没有或页面跳转路径不对")
    })
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        try {
            List<Message> messages = request.getMessages();

            Map<String, Object> requestBody = Map.of(
                    "messages", messages,
                    "model", "grok-beta",
                    "stream", false,
                    "temperature", 0,
                    "user", userRepository.findByUid(request.getUid()).getUserAccount()
            );

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(CHAT_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            logger.info("AI Response: {}", response.body());
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                JsonNode contentNode = messageNode.path("content");
                String content = contentNode.asText();
                logger.info("Content: {}", content);
                request.getMessages().add(new Message("system", content));
            } else {
                logger.info("No choices available");
            }

            SaveChat(request);
            return ResponseEntity.ok(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("服务器内部错误");
        }
    }

    private void SaveChat(ChatRequest request) {
        Optional<ChatRequestCopy> existingChatRequestCopy = Optional.ofNullable(chatRequestCopyRepository.findByChatid(request.getChatid()));
        ChatRequestCopy chatRequestCopy;
        if (existingChatRequestCopy.isPresent()) {
            chatRequestCopy = existingChatRequestCopy.get();
            chatRequestCopy.setUid(request.getUid());
            chatRequestCopy.setMessages(request.getMessages());
        } else {
            chatRequestCopy = new ChatRequestCopy(request);
        }
        chatRequestCopyRepository.save(chatRequestCopy);
    }

    /**
     * 获取绘画ai的访问令牌
     * @return
     * @throws Exception
     */
    public String getAccessToken() throws Exception {
        Map<String, String> params = Map.of(
                "grant_type", "client_credentials",
                "client_id", CLIENT_ID,
                "client_secret", CLIENT_SECRET
        );

        String form = params.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(ACCESS_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readTree(response.body()).get("access_token").asText();
    }


    @Operation(summary = "测试AI绘画的注解方法Post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "请求成功"),
            @ApiResponse(responseCode = "400", description = "请求参数没填好"),
            @ApiResponse(responseCode = "401", description = "没有权限"),
            @ApiResponse(responseCode = "403", description = "禁止访问"),
            @ApiResponse(responseCode = "404", description = "请求路径没有或页面跳转路径不对")
    })
    @PostMapping("/art")
    public ResponseEntity<?> art(@RequestBody Map<String, String> params) throws Exception {
        String accessToken = getAccessToken();
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/text2image/sd_xl?access_token=" + accessToken;

        Map<String, Object> requestBody = Map.of(
                "prompt", params.get("prompt"),
                "negative_prompt", params.get("negative_prompt"),
                "size", params.get("size"),
                "steps", Integer.parseInt(params.get("steps")),
                "n", Integer.parseInt(params.get("n")),
                "style", params.get("style"),
                "sampler_index", params.get("sampler_index")
        );

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(100))
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return ResponseEntity.ok(response.body());
    }

//    @PostMapping("/music")
//    public ResponseEntity<?> Music(){
//        try {
//            Map<String, Object> requestBody = Map.of(); // Add your request body content here
//
//            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI("https://api.acedata.cloud/suno/audios"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
//                    .build();
//
//            HttpClient client = HttpClient.newHttpClient();
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            System.out.println(response.body());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Operation(summary = "测试Swagger3注解方法Get")
    @Parameters({@Parameter(name = "uid",description = "编码")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "请求成功"),
            @ApiResponse(responseCode = "400", description = "请求参数没填好"),
            @ApiResponse(responseCode = "401", description = "没有权限"),
            @ApiResponse(responseCode = "403", description = "禁止访问"),
            @ApiResponse(responseCode = "404", description = "请求路径没有或页面跳转路径不对")
    })
    @GetMapping("/getchats")
    public ResponseEntity<?> getChats(@RequestParam int uid) {
        List<ChatRequestCopy> chatRequestCopies = chatRequestCopyRepository.findAllByUid(uid);
        return ResponseEntity.ok(chatRequestCopies);
    }


}

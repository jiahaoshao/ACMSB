package net.fangyi.acmsb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
@CrossOrigin //解决跨域问题
public class AiController {


    private static final String CLIENT_ID = "RNLjFTsINgFUlLhiQotE1X03"; // Replace with your API Key
    private static final String CLIENT_SECRET = "t12jZNww9mE6OQMZQFOC0ZItw5gwpZX4"; // Replace with your Secret Key
    private static final String ACCESS_URL = "https://aip.baidubce.com/oauth/2.0/token";

    private static final String API_KEY = "xai-6mIqkZJWw3A5WawUuTz5tQ1dcx08IoGQfT09MDJNnJ17bjxX2vbZF0HZtWQ428z1FbzOGNLLzcczTQF5";
    private static final String CHAT_URL = "https://api.x.ai/v1/chat/completions";
    private static final ObjectMapper objectMapper = new ObjectMapper();



    /**
     * 聊天AI
     * @param systemContent 系统消息
     * @param userContent 用户消息
     * @return
     * @throws Exception
     */
    @GetMapping("/chat")
    public ResponseEntity<?> chat(@RequestParam String systemContent, @RequestParam String userContent) throws Exception {
        Map<String, Object> requestBody = Map.of(
                "messages", List.of(
                        Map.of("role", "system", "content", systemContent),
                        Map.of("role", "user", "content", userContent)
                ),
                "model", "grok-beta"
        );

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(CHAT_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return ResponseEntity.ok(response.body());
    }

    /**
     * 获取绘画ai的访问令牌
     * @return
     * @throws Exception
     */
    @PostMapping("/get_access_token")
    public ResponseEntity<?> getAccessToken() throws Exception {
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

        return ResponseEntity.ok(response.body());
    }


    @PostMapping("/art")
    public ResponseEntity<?> art(@RequestBody Map<String, String> params) throws Exception {
        String accessToken = params.get("access_token");
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/text2image/sd_xl?access_token=" + accessToken;

        Map<String, Object> requestBody = Map.of(
                "prompt", params.get("prompt"),
                "negative_prompt", "white",
                "size", "1024x1024",
                "steps", 20,
                "n", 1,
                "sampler_index", "DPM++ SDE Karras"
        );

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(100))
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return ResponseEntity.ok(response.body());
    }
}

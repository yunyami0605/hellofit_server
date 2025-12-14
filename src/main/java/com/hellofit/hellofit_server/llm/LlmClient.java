package com.hellofit.hellofit_server.llm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class LlmClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${hellofit.llm.base-url:http://localhost:8081}")
    private String baseUrl;

    @Value("${hellofit.llm.api-key:}")
    private String apiKey;

    public record ChatMessage(String role, String content) {}
    public record ChatRequest(List<ChatMessage> messages, String model, Double temperature) {}

    public String generate(String prompt) {
        String url = baseUrl + "/generate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("Authorization", "Bearer " + apiKey);
        }
        Map<String, Object> body = Map.of("prompt", prompt);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        @SuppressWarnings("unchecked")
        Map<String, Object> resp = restTemplate.postForObject(url, req, Map.class);
        if (resp == null) return "죄송해요. 지금은 답변을 제공할 수 없어요.";
        Object text = resp.getOrDefault("text", "");
        String s = String.valueOf(text);
        if (s.isBlank()) s = "죄송해요. 지금은 답변을 제공할 수 없어요.";
        return s;
    }

    public String chat(List<ChatMessage> messages) {
        String url = baseUrl + "/chat";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("Authorization", "Bearer " + apiKey);
        }
        ChatRequest body = new ChatRequest(messages, null, null);
        HttpEntity<ChatRequest> req = new HttpEntity<>(body, headers);
        @SuppressWarnings("unchecked")
        Map<String, Object> resp = restTemplate.postForObject(url, req, Map.class);
        if (resp == null) return "죄송해요. 지금은 답변을 제공할 수 없어요.";
        Object text = resp.getOrDefault("text", "");
        String s = String.valueOf(text);
        if (s.isBlank()) s = "죄송해요. 지금은 답변을 제공할 수 없어요.";
        return s;
    }

    /**
     * SSE 스트리밍으로 delta를 수신하여 콜백으로 전달.
     * 마지막에 [DONE]을 만나면 true 반환.
     */
    public void streamGenerate(String prompt, Consumer<String> onDelta) {
        BufferedReader reader = null;
        try {
            String endpoint = baseUrl + "/generate/stream";
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            if (apiKey != null && !apiKey.isBlank()) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            String payload = "{\"prompt\":\"" + prompt.replace("\"", "\\\"") + "\"}";
            conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
            conn.getOutputStream().flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    if ("[DONE]".equals(data)) {
                        break;
                    }
                    // data는 {"delta":"..."} JSON
                    int idx = data.indexOf("\"delta\":");
                    if (idx >= 0) {
                        int start = data.indexOf('"', idx + 8);
                        int end = data.lastIndexOf('"');
                        if (start >= 0 && end > start) {
                            String delta = data.substring(start + 1, end);
                            onDelta.accept(delta);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception ignored) {}
        }
    }
}



package com.hellofit.hellofit_server.llm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LlmClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${hellofit.llm.base-url:http://localhost:8081}")
    private String baseUrl;

    @Value("${hellofit.llm.api-key:}")
    private String apiKey;

    public String generate(String prompt) {
        String url = baseUrl + "/generate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("Authorization", "Bearer " + apiKey);
        }
        Map<String, Object> body = Map.of("prompt", prompt);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        Map resp = restTemplate.postForObject(url, req, Map.class);
        if (resp == null) return "죄송해요. 지금은 답변을 제공할 수 없어요.";
        Object text = resp.getOrDefault("text", "");
        String s = String.valueOf(text);
        if (s.isBlank()) s = "죄송해요. 지금은 답변을 제공할 수 없어요.";
        return s;
    }
}



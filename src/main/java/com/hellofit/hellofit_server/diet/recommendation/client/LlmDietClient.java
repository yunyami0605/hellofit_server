package com.hellofit.hellofit_server.diet.recommendation.client;

import com.hellofit.hellofit_server.diet.recommendation.dto.LLMDietRequestDto;
import com.hellofit.hellofit_server.diet.recommendation.dto.LLMDietResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmDietClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8001/recommend/diet";

    public LLMDietResponseDto.DietRecommendationDto[] regenerateDay(LocalDate date, LLMDietRequestDto.DietAutoRequest req) {
        String url = baseUrl + "/" + date + "/regenerate/day";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LLMDietRequestDto.DietAutoRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<LLMDietResponseDto.DietRecommendationDto[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, LLMDietResponseDto.DietRecommendationDto[].class);

        LLMDietResponseDto.DietRecommendationDto[] body = response.getBody();

        if (body == null) {
            log.warn("LLM 서버 응답이 null입니다. url={}, status={}", url, response.getStatusCode());
        } else {
            log.info("LLM 서버 응답 = {}", Arrays.toString(body));
        }

        return response.getBody();
    }

}

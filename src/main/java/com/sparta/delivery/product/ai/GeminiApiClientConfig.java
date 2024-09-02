package com.sparta.delivery.product.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GeminiApiClientConfig {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String generateContent(String prompt) {
        String url = String.format(
            "%s/v1beta/models/gemini-1.5-flash-latest:generateContent?key=%s", geminiApiUrl,
            geminiApiKey);

        // JSON 요청 본문 구성
        String requestBody = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
            prompt);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // HTTP 엔티티 설정 (헤더와 본문 포함)
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate을 사용하여 POST 요청 보내기
        ResponseEntity<String> responseEntity = restTemplate().exchange(url, HttpMethod.POST,
            requestEntity, String.class);

        // 응답 상태 확인 및 본문 반환
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("API로 부터 호출을 실패하였습니다.");
        }
    }
}


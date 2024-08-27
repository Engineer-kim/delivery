package com.sparta.delivery.product.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GeminiService {

    private final GeminiApiClientConfig geminiApiClientConfig;
    private final AiRepository aiRepository;

    private static final int MAX_INPUT_LENGTH = 50; // 최대 입력 길이
    private static final String INSTRUCTION = "답변을 최대한 간결하게 50자 이하로.";

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;


    @Transactional
    public String generateProductDescription(String prompt) {
        // 요청 텍스트 길이 제한
        if (prompt.length() > MAX_INPUT_LENGTH) {
            prompt = prompt.substring(0, MAX_INPUT_LENGTH);
        }

        // 요청 텍스트에 추가 지시사항 삽입
        String fullPrompt = prompt + " " + INSTRUCTION;

        // API 호출을 통해 상품 설명 생성
        String response = geminiApiClientConfig.generateContent(fullPrompt);

        // 요청 및 응답을 데이터베이스에 저장
        saveRequestResponse(fullPrompt, response);

        return response;
    }

    private void saveRequestResponse(String request, String response) {
        Ai aiRequestResponse = new Ai();
        aiRequestResponse.setRequestText(request);
        aiRequestResponse.setResponseText(response);

        aiRepository.save(aiRequestResponse);
    }
}
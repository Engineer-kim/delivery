package com.sparta.delivery.product.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/generate-description")
    public ResponseEntity<String> generateProductDescription(@RequestBody AiDto aiDto) {
        String description = geminiService.generateProductDescription(aiDto.prompt());
        return ResponseEntity.status(HttpStatus.CREATED).body(description);
    }
}

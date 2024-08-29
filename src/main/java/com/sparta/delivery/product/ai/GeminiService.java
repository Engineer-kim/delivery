package com.sparta.delivery.product.ai;

import com.sparta.delivery.product.dto.PageDto;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.repo.ShopRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GeminiService {

    private final GeminiApiClientConfig geminiApiClientConfig;
    private final AiRepository aiRepository;
    private final ShopRepository shopRepository;

    private static final int MAX_INPUT_LENGTH = 50; // 최대 입력 길이
    private static final String INSTRUCTION = "답변을 최대한 간결하게 50자 이하로.";

    @Transactional
    public String generateProductDescription(String prompt, String shopId) {
        // 요청 텍스트 길이 제한
        if (prompt.length() > MAX_INPUT_LENGTH) {
            prompt = prompt.substring(0, MAX_INPUT_LENGTH);
        }

        // 요청 텍스트에 추가 지시사항 삽입
        String fullPrompt = prompt + " " + INSTRUCTION;

        // API 호출을 통해 상품 설명 생성
        String response = geminiApiClientConfig.generateContent(fullPrompt);

        // 요청 및 응답을 데이터베이스에 저장
        saveRequestResponse(fullPrompt, response, shopId);

        return response;
    }

    private void saveRequestResponse(String request, String response, String shopId) {
        Ai aiRequestResponse = new Ai();
        if(shopId != null){
            Store shop = shopRepository.findById(UUID.fromString(shopId)).orElseThrow(() -> new RuntimeException("가게 id를 찾을 수 없습니다.") );
            aiRequestResponse.setStore(shop);
        }

        aiRequestResponse.setRequestText(request);
        aiRequestResponse.setResponseText(response);

        aiRepository.save(aiRequestResponse);
    }

    // New method to delete all entries with a specific shop_id
    @Transactional
    public void deleteById(String id) {
        Optional<Ai> ai = aiRepository.findById(UUID.fromString(id));
        if (ai.isPresent()) {
            ai.get().setDeleted(true);
        }
        else {
            throw new RuntimeException("ai id를 찾을 수 없습니다.");
        }
    }

    // New method to delete all entries where shop_id is null
    @Transactional
    public void deleteWhereShopIdIsNull() {
        List<Ai> aiList = aiRepository.findByStoreIsNull();

        for(Ai ai : aiList) {
            ai.setDeleted(true);
        }
    }

    // New method to find all entries by shop_id
    public PageDto findByShopId(String shopId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
            Sort.by("createdAt").descending());
        Page<Ai> aiPage = aiRepository.findAllByStoreShopId(UUID.fromString(shopId), sortedPageable);

        var data = aiPage.getContent().stream()
            .map(AiResponseDto::new)
            .toList();

        return new PageDto(data,
            aiPage.getTotalElements(),
            aiPage.getTotalPages(),
            pageable.getPageNumber(),
            data.size()
        );
    }
}
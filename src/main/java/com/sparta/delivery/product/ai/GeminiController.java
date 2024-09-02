package com.sparta.delivery.product.ai;

import com.sparta.delivery.product.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER') or hasRole('MASTER')")
    @PostMapping("/generate-description")
    public ResponseEntity<String> generateProductDescription(@RequestBody AiDto aiDto) {
        String description = geminiService.generateProductDescription(aiDto.prompt(),
            aiDto.shopId());
        return ResponseEntity.status(HttpStatus.CREATED).body(description);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('OWNER') or hasRole('MASTER')")
    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        geminiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('MASTER')")
    @DeleteMapping("/delete/null")
    public ResponseEntity<Void> deleteWhereShopIdIsNull() {
        geminiService.deleteWhereShopIdIsNull();
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/search/{shopId}")
    public ResponseEntity<PageDto> findByShopId(@PathVariable String shopId, Pageable pageable) {
        int size = pageable.getPageSize();

        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

        PageDto aiList = geminiService.findByShopId(shopId, pageable);
        return ResponseEntity.ok(aiList);
    }

}

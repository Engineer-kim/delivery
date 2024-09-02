package com.sparta.delivery.product.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAddResponseDto {

    private UUID productId;
    private String productName;
    private String description;
    private int price;
    private UUID storeId;
    private LocalDateTime createdAt;
    private String createdBy;
}

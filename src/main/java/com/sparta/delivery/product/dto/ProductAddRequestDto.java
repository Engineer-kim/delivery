package com.sparta.delivery.product.dto;

public record ProductAddRequestDto(
    String shopId,
    String productName,
    String description,
    int price
) {

}

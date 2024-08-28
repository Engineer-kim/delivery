package com.sparta.delivery.product.dto;

public record ProductAddRequestDto(
    String productName,
    String description,
    int price
) {

}

package com.sparta.delivery.product.dto;

import com.sparta.delivery.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAddResponseDto {
    private Product product;
}
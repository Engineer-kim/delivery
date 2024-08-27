package com.sparta.delivery.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private UUID cartItemId; //장바구니 상품 아이디

    private UUID productId; // 상품 아이디

    private String productName; //상품명

    private int price; //상품 금액

    private int quantity; //수량 조인
}

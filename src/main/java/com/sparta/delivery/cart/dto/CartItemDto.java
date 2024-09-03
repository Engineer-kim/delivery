package com.sparta.delivery.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class CartItemDto {
    private UUID productId;
    private int quantity;
}

package com.sparta.delivery.shop.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ShopResponse {
    private int statusCode;
    private String status;
    private String message;
    private ShopData data;
}

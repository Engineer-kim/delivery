package com.sparta.delivery.shop.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ShopData {
    private UUID shopId;
    private String shopName;
    private String shopAddress;
    private String shopType;
    private LocalTime shopOpenTime;
    private LocalTime shopClosedTime;
    private String shopPhone;
    private Long userId;
}

package com.sparta.delivery.shop.dto;

import com.sparta.delivery.shop.statusEnum.shopType.ShopType;
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
    private ShopType shopType;
    private LocalTime shopOpenTime;
    private LocalTime shopClosedTime;
    private String shopPhone;
    private Long userId;
}

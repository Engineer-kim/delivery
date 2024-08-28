package com.sparta.delivery.shop.dto;

import com.sparta.delivery.shop.statusEnum.shopType.ShopType;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShopRequest{
    /**가게 이름*/
    private String shopName;
    /**가게 주소*/
    private String shopAddress;
    /**가게 타입*/
    private ShopType shopType;
    /**가게 오픈 시각*/
    private LocalTime shopOpenTime;
    /**가게 종료 시각*/
    private LocalTime  shopCloseTime;
    /**가게 연락처*/
    private String shopPhone;

    private Long userId;

    private UUID productId;
}

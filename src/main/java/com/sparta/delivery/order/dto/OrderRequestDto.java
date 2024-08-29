package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.OrderTypeEnum;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderRequestDto {
    private UUID shopId;
    private OrderTypeEnum typeEnum;
    private String request;
}

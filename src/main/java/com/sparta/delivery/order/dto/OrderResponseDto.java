package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.OrderStatusEnum;
import com.sparta.delivery.order.OrderTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponseDto {
    private UUID orderId;
    private Long userId;
    private OrderTypeEnum typeEnum;
    private OrderStatusEnum statusEnum;
    private List<OrderItemDto> orderItems;
    private String request;
    private Integer totalAmount;
}

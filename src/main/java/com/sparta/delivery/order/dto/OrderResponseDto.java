package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.OrderItem;
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
    private List<OrderItem> orderItems;
    private int quantity;
    private String request;
    private Integer totalAmount;
}

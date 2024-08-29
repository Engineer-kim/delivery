package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.OrderStatusEnum;
import com.sparta.delivery.order.OrderTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderResponseDto {
    private UUID orderId;
    private UUID shopId;
    private String shopName;
    private Long userId;
    private LocalDateTime createdAt; // 주문 일자
    private OrderTypeEnum typeEnum;
    private OrderStatusEnum statusEnum;
    private List<OrderItemDto> orderItems;
    private String request;
    private Integer totalAmount;
}

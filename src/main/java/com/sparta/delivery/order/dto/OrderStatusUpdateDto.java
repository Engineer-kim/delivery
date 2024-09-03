package com.sparta.delivery.order.dto;

import com.sparta.delivery.order.OrderStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderStatusUpdateDto {
    private UUID orderId;
    private OrderStatusEnum newStatus;
}

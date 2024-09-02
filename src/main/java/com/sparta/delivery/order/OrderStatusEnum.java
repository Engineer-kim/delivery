package com.sparta.delivery.order;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    PENDING("진행 중"), COMPLETED("완료"), CANCELED("취소");

    private final String description;

    OrderStatusEnum(String description) {
        this.description = description;
    }
}

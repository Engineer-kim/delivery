package com.sparta.delivery.order;

import lombok.Getter;

@Getter
public enum OrderTypeEnum {
    DELIVERY("배달 주문"), PICKUP ("포장 주문");

    private final String description;

    OrderTypeEnum(String description) {
        this.description = description;
    }
}

package com.sparta.delivery.order;

import com.sparta.delivery.user.User;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "p_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // 총 가격
    @Column(name = "total_amount")
    private Integer totalAmount;

    // 주문 종류(배달 주문 or 포장 주문)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OrderTypeEnum type;

    // 배달 상태(진행중, 완료, 취소)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEnum status;

    // 요청사항
    @Column(name = "request", length = 100)
    private String request;



    // 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

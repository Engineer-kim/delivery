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


    @Column(name = "total_amount")
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OrderTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEnum status;

    @Column(name = "request", length = 100)
    private String request;


    // 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

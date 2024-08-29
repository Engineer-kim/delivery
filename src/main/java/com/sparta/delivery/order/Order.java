package com.sparta.delivery.order;

import com.sparta.delivery.address.UserAddress;
import com.sparta.delivery.common.TimeStamped;
import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_orders")
public class Order extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // 총 가격
    @Column(name = "total_amount")
    private Integer totalAmount;

    // 주문 종류(배달 주문 or 포장 주문)
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OrderTypeEnum type;

    // 주문 상태(주문 전, 진행중, 완료, 취소)
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatusEnum status;

    // 요청사항
    @Column(name = "request", length = 100)
    private String request;

    // 삭제 여부
    @Column(name = "is_deleted")
    private boolean isDeleted = false;


    // 주문 총 금액 계산
    public Integer calculateTotalAmount() {
        return orderItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    // 외래키
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")  // 단방향 연관관계 설정
    private UserAddress address;

}

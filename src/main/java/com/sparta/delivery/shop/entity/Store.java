package com.sparta.delivery.shop.entity;

import com.sparta.delivery.common.TimeStamped;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Store extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /**샵 아이디 식별자*/
    private UUID shopId;

    @Column(nullable = false, length = 30)
    /**가게 이름*/
    private String shopName;

    @Column(nullable = false, length = 100)
    /**가게 주소*/
    private String shopAddress;

    @Column(nullable = false, length = 30)
    /**가게 타입*/
    private String shopType;

    @Column(nullable = false)
    /**가게 오픈 시각*/
    private LocalTime shopOpenTime;

    @Column(nullable = false)
    /**가게 종료 시각*/
    private LocalTime shopCloseTime;

    @Column(nullable = false, length = 15)
    /**가게 연락처*/
    private String shopPhone;
    /**프로덕트와 연관관계*/
    @OneToMany(mappedBy = "store")
    private List<Product> products;
    /**유저 아이디*/
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)  // User 엔티티와 관계 설정
    private User user;
}

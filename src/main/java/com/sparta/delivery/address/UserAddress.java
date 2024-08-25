package com.sparta.delivery.address;

import com.sparta.delivery.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_addresses")
public class UserAddress {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    // 배송지 이름 (집, 회사 등등..)
    @Column(name = "address_name", length = 100, nullable = false)
    private String addressName;

    // 기본 주소
    @Column(name = "address_line1", nullable = false)
    private String line1;

    // 상세 주소 (아파트 동, 호수 등)
    @Column(name = "address_line2", length = 255)
    private String line2;



    // 외래키
    // User 가 여러개의 배송지를 가질 수 있는 일대다 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

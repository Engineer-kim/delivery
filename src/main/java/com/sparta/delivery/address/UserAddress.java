package com.sparta.delivery.address;

import com.sparta.delivery.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_address")
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "address_name", length = 100, nullable = false)
    private String addressName;

    // 기본 주소
    @Column(name = "address_line1", length = 255, nullable = false)
    private String line1;

    // 상세 주소 (아파트 동, 호수 등)
    @Column(name = "address_line2", length = 255)
    private String line2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

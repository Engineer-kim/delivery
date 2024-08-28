package com.sparta.delivery.shop.entity;

import com.sparta.delivery.common.TimeStamped;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.shop.statusEnum.ShopDataStatus;
import com.sparta.delivery.shop.statusEnum.ShopPrivacyStatus;
import com.sparta.delivery.shop.statusEnum.shopType.ShopType;
import com.sparta.delivery.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.sparta.delivery.shop.statusEnum.ShopPrivacyStatus.P;

@Entity
@Table(name = "p_store")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class Store extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_seq")
    @SequenceGenerator(name = "shop_seq", sequenceName = "shop_sequence", allocationSize = 1)
    /**샵 아이디 식별자*/
    private Long shopId;

    @Column(nullable = false, length = 30)
    /**가게 이름*/
    private String shopName;

    @Column(nullable = false, length = 100)
    /**가게 주소*/
    private String shopAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    /**가게 타입*/
    private ShopType shopType;

    @Column(nullable = false)
    /**가게 오픈 시각*/
    private LocalTime shopOpenTime;

    @Column(nullable = false)
    /**가게 종료 시각*/
    private LocalTime shopCloseTime;

    @Column(nullable = false, length = 15)
    /**가게 연락처*/
    private String shopPhone;

    /**삭제 여부 판단*/
    @Enumerated(EnumType.STRING)
    @Column(name = "db_sts",nullable = false , columnDefinition = "VARCHAR(1)")
    private ShopDataStatus deleteStatus;

    /**공개 여부*/
    @Enumerated(EnumType.STRING)
    @Column(name = "public_sts", nullable = false, columnDefinition = "VARCHAR(1)")
    private ShopPrivacyStatus privacyStatus;
    
    /**프로덕트와 연관관계*/
    @OneToMany(mappedBy = "store")
    private List<Product> products;
    /**유저 아이디*/
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @PrePersist
    private void setDefaultStatus() {
        if (this.deleteStatus == null) {
            this.deleteStatus = ShopDataStatus.U; // 기본값 설정
        }
        if (this.privacyStatus == null) {
            this.privacyStatus = ShopPrivacyStatus.P; // 기본값 설정
        }
    }
}

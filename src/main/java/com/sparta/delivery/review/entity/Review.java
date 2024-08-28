package com.sparta.delivery.review.entity;

import com.sparta.delivery.common.statusEnum.DataStatus;
import com.sparta.delivery.common.statusEnum.PrivacyStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "p_review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    /**리뷰 내용*/
    @Column(name = "review_content", length = 150, nullable = false)
    private String reviewContent;

    /**리뷰 별점*/
    @Column(name = "review_rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal reviewRating;

    /**메뉴 아이디*/
    @Column(name = "menu_id")
    private Integer menuId;

    /**삭제 여부 판단*/
    @Enumerated(EnumType.STRING)
    @Column(name = "db_sts",nullable = false , columnDefinition = "VARCHAR(1)")
    private DataStatus deleteStatus;

    /**공개 여부*/
    @Enumerated(EnumType.STRING)
    @Column(name = "public_sts", nullable = false, columnDefinition = "VARCHAR(1)")
    private PrivacyStatus privacyStatus;

    /**사용자 아이디*/
    @Column(name = "user_id")
    private String userId;

    @PrePersist
    private void setDefaultStatus() {
        if (this.deleteStatus == null) {
            this.deleteStatus = DataStatus.U; // 기본값 설정
        }
        if (this.privacyStatus == null) {
            this.privacyStatus = PrivacyStatus.P; // 기본값 설정
        }
    }
}
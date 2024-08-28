package com.sparta.delivery.review.entity;

import com.sparta.delivery.common.TimeStamped;
import com.sparta.delivery.common.statusEnum.DataStatus;
import com.sparta.delivery.common.statusEnum.PrivacyStatus;
import com.sparta.delivery.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_review")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
public class Review extends TimeStamped {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID reviewId;

    //리뷰 내용
    @Column(name = "review_content", length = 150, nullable = false)
    private String reviewContent;

    //리뷰 제목
    @Column(name = "review_title", length = 150, nullable = false)
    private String reviewTitle;

    //리뷰 별점
    @Column(name = "review_rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal reviewRating;

    //삭제 여부 판단
    @Enumerated(EnumType.STRING)
    @Column(name = "db_sts",nullable = false , columnDefinition = "VARCHAR(1)")
    private DataStatus deleteStatus;

    //공개 여부
    @Enumerated(EnumType.STRING)
    @Column(name = "public_sts", nullable = false, columnDefinition = "VARCHAR(1)")
    private PrivacyStatus privacyStatus;

    //사용자 아이디
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User user;

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
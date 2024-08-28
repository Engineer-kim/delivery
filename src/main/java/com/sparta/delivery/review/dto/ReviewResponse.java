package com.sparta.delivery.review.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewResponse {
    private UUID reviewId;
    private String reviewContent;
    private String reviewTitle;
    private double reviewRating;
    //주의: p_user  테이블의 userName 을 지칭 userId 랑 다름
    private String userID;
}

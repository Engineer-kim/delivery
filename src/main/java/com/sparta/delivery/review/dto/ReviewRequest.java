package com.sparta.delivery.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    @JsonProperty("order_id")
    private UUID orderId;
    @JsonProperty("review_title")
    private String reviewTitle;
    @JsonProperty("review_rating")
    private double reviewRating;
    @JsonProperty("review_content")
    private String reviewContent;
}

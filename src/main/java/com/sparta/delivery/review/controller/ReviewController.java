package com.sparta.delivery.review.controller;

import com.sparta.delivery.common.ApiResponse;
import com.sparta.delivery.review.dto.ReviewRequest;
import com.sparta.delivery.review.dto.ReviewResponse;
import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.review.service.ReviewService;
import com.sparta.delivery.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;


    //리뷰 작성
    @PostMapping("/review")
    public ResponseEntity<ApiResponse> addStore(@RequestBody ReviewRequest reviewRequest,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        System.out.println("ReviewRequest: " + reviewRequest);
        reviewService.addReview(userId,reviewRequest);
        ApiResponse response = new ApiResponse(201, "success", "리뷰가 성공적으로 작성되었습니다.", reviewRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //리뷰 삭제
    @DeleteMapping("/review/{id}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        if (reviewService.isDeleteReview(id)) {
            ApiResponse response = new ApiResponse(404, "not found", "해당 리뷰는 이미 삭제되었습니다", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            reviewService.deleteReview(id, userId, userDetails);
            ApiResponse response = new ApiResponse(200, "success", "리뷰가 삭제되었습니다.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
    
    //삭제 혹은 비공개 제외 리뷰 전체 조회 권한과 관계 없이 다 접근가능
    @GetMapping("/review")
    public ResponseEntity<ApiResponse> getAllReview() {
        List<Review> result = reviewService.getAllReview();
        ApiResponse response = new ApiResponse(200, "success", "null", result != null ? result : Collections.emptyList());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //자신이 작성한 리뷰조회
    @GetMapping("/review/{id}")
    public ResponseEntity<ApiResponse> getOwnReview(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        Optional<Review> result = reviewService.getOwnReview(id, userId);
        //복잡하게 응답객체를 별도로 만든 이유:
        // 단순히 DB 조회후 반환시 유저객체의 유저 비밀번호와 같은 민감한 정보 응답객체로 보임
        //그리하여 민감한정보 제외 응답에 보이도록
        if (result.isPresent()) {
            Review review = result.get();
            ReviewResponse reviewResponse = reviewService.createReviewResponse(review, userDetails.getUsername());
            ApiResponse response = new ApiResponse(200, "success", null, reviewResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            ApiResponse response = new ApiResponse(404, "not found", "리뷰를 찾을 수 없습니다.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

//    eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGFuZzAxMDMiLCJhdXRoIjoiQ1VTVE9NRVIiLCJleHAiOjE3MjQ4MjcxMjksImlhdCI6MTcyNDgyMzUyOX0.vPZQeSfhRJ_Kl-lKYt8P-QucX_heWMbYKwJq4ACrGdQ
    //리뷰 비공개 처리
    @DeleteMapping("/review/{id}/privacy")
    public ResponseEntity<ApiResponse> makePrivacyReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        if (reviewService.isPrivacyReview(id)) {
            ApiResponse response = new ApiResponse(404, "not found", "해당 리뷰는 이미 비공개 처리 되었습니다", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            reviewService.makePrivacyReview(id, userId, userDetails);
            ApiResponse response = new ApiResponse(200, "success", "리뷰가 비공개 처리 되었습니다.", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
}

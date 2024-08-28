package com.sparta.delivery.review.service;

import com.sparta.delivery.common.statusEnum.DataStatus;
import com.sparta.delivery.common.statusEnum.PrivacyStatus;
import com.sparta.delivery.review.dto.ReviewRequest;
import com.sparta.delivery.review.dto.ReviewResponse;
import com.sparta.delivery.review.entity.Review;
import com.sparta.delivery.review.repo.ReviewRepository;
import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRepository;
import com.sparta.delivery.user.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;


    public void addReview(Long userId, ReviewRequest reviewRequest) {
        User user = getUserAndCheckAuthorization(userId);
        Review review = Review.builder()
                .reviewTitle(reviewRequest.getReviewTitle())
                .reviewRating(BigDecimal.valueOf(reviewRequest.getReviewRating()))
                .reviewContent(reviewRequest.getReviewContent())
                .userId(userId)
                .build();
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(UUID id, Long userId, UserDetailsImpl userDetails) {
        Optional<Review> findReview = reviewRepository.findById(id);
        if (findReview.isPresent()) {
            Review review = findReview.get();
            if (review.getUserId().equals(userId) ||
                    userDetails.getUser().getRole() == UserRoleEnum.MASTER ||
                    userDetails.getUser().getRole() == UserRoleEnum.OWNER) {
                reviewRepository.updateDbSts(id, DataStatus.D);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
            }
        } else {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "작성된 리뷰가 없습니다");
        }
    }

    @Transactional
    public void makePrivacyReview(UUID id, Long userId, UserDetailsImpl userDetails) {
        Optional<Review> findReview = reviewRepository.findById(id);
        if (findReview.isPresent()) {
            Review review = findReview.get();
            if (review.getUserId().equals(userId) ||
                    userDetails.getUser().getRole() == UserRoleEnum.MASTER ||
                    userDetails.getUser().getRole() == UserRoleEnum.OWNER) {
                reviewRepository.updatePrivacySts(id, PrivacyStatus.R);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
            }
        } else {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "작성된 리뷰가 없습니다");
        }
    }


    public List<Review> getAllReview() {
       return reviewRepository.findAllByDbStsAndPrivacy(DataStatus.U , PrivacyStatus.P);
    }

    public Optional<Review> getOwnReview(UUID id, Long userId) {
        return reviewRepository.findByIdAndUserId(id , DataStatus.U , userId );
    }

    public ReviewResponse createReviewResponse(Review review, String username) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .reviewContent(review.getReviewContent())
                .reviewTitle(review.getReviewTitle())
                .reviewRating(review.getReviewRating().doubleValue())
                .userID(username)
                .build();
    }


    public boolean isDeleteReview(UUID id) {
        Optional<Review> result = reviewRepository.findByIdAnDbSts(id , DataStatus.D);
        return result.isPresent();
    }

    public boolean isPrivacyReview(UUID id) {
        Optional<Review> result = reviewRepository.findByIdAndPrivacy(id , PrivacyStatus.R);
        return result.isPresent();
    }



    private boolean isUserAuthorized(UserRoleEnum role) {
        return role == UserRoleEnum.CUSTOMER;
    }

    private User getUserAndCheckAuthorization(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"사용자를 찾을 수 없습니다."));

        if (!isUserAuthorized(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "고객만 리뷰를 작성 할 수 있습니다");
        }

        return user;
    }



}

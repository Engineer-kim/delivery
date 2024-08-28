package com.sparta.delivery.review.repo;

import com.sparta.delivery.common.statusEnum.DataStatus;
import com.sparta.delivery.common.statusEnum.PrivacyStatus;
import com.sparta.delivery.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Modifying
    @Query("UPDATE Review r SET r.deleteStatus = :deleteStatus WHERE r.reviewId = :reviewId")
    void updateDbSts(@Param("reviewId") UUID reviewId, @Param("deleteStatus") DataStatus deleteStatus);

    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.deleteStatus = :deleteStatus")
    Optional<Review> findByIdAnDbSts(@Param("reviewId") UUID reviewId, @Param("deleteStatus") DataStatus deleteStatus);

    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.deleteStatus = :deleteStatus AND r.userId = :userId")
    Optional<Review> findByIdAndUserId(@Param("reviewId") UUID reviewId, @Param("deleteStatus") DataStatus deleteStatus,  @Param("userId") Long userId);

    @Query("SELECT r FROM Review r WHERE r.deleteStatus = :deleteStatus AND r.privacyStatus = :privacyStatus")
    List<Review> findAllByDbStsAndPrivacy(@Param("deleteStatus") DataStatus dataStatus ,@Param("privacyStatus") PrivacyStatus privacyStatus);

    @Modifying
    @Query("UPDATE Review r SET r.privacyStatus = :privacyStatus WHERE r.reviewId = :reviewId")
    void updatePrivacySts(@Param("reviewId") UUID reviewId, @Param("privacyStatus") PrivacyStatus privacyStatus);

    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.privacyStatus = :privacyStatus")
    Optional<Review> findByIdAndPrivacy(@Param("reviewId") UUID reviewId, @Param("privacyStatus") PrivacyStatus privacyStatus);
}

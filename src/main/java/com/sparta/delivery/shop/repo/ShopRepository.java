package com.sparta.delivery.shop.repo;

import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.common.statusEnum.DataStatus;
import com.sparta.delivery.common.statusEnum.PrivacyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Store , Long>{

    @Query("SELECT s FROM Store s WHERE s.shopId = :shopId  AND s.deleteStatus = :deleteStatus")
    Optional<Store> findByIdAndDeleteStatus(@Param("shopId") Long shopId , @Param("deleteStatus") DataStatus deleteStatus);

    @Query("SELECT s FROM Store s WHERE s.shopId = :shopId  AND s.privacyStatus = :privacyStatus")
    Optional<Store> findByIdAndPrivacyStatus(@Param("shopId") Long shopId , @Param("privacyStatus") PrivacyStatus shopPrivacyStatus);
}

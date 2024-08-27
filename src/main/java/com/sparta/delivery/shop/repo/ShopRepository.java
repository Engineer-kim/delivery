package com.sparta.delivery.shop.repo;

import com.sparta.delivery.shop.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Store , Long>{
    Optional<Store> findByShopId(UUID id);
}

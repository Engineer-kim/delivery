package com.sparta.delivery.shop.repo;

import com.sparta.delivery.shop.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Store , Long>{
}

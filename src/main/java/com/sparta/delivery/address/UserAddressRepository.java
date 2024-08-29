package com.sparta.delivery.address;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
    List<UserAddress> findByUserId(Long userId);

    List<UserAddress> findByUserIdAndIsDeletedFalse(Long userId);

    List<UserAddress> findAllByLine1ContainingIgnoreCase(String searchKeyword);

    Page<UserAddress> findByLine1ContainingIgnoreCase(String keyword, Pageable pageable);
}

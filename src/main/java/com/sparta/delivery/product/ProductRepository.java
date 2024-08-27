package com.sparta.delivery.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.springframework.lang.Nullable;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAll(@Nullable Pageable pageable);

    Page<Product> findByProductNameContaining(String search, Pageable pageable);
}


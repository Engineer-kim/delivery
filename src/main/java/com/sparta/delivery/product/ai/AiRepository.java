package com.sparta.delivery.product.ai;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRepository extends JpaRepository<Ai, UUID> {

    Page<Ai> findAllByStoreShopId(UUID shopId, Pageable pageable);

    List<Ai> findByStoreIsNull();

}


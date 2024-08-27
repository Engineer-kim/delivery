package com.sparta.delivery.product.ai;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRepository extends JpaRepository<Ai, Long> {
    // 추가적인 쿼리 메서드가 필요하다면 여기서 정의
}


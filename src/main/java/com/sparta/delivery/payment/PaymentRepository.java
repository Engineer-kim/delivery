package com.sparta.delivery.payment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    // 가맹점 id로 결제내역 찾기 - 가장 최근의 결제를 가져옵니다.
    List<Payment> findTopByPartnerUserIdOrderByCreatedAtDesc(String partnerUserId);

    // 특정 사용자에 대한 결제 내역을 페이징된 결과로 반환합니다.
    //Page<Payment> findByUserId(UUID userId, Pageable pageable);
}


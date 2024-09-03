package com.sparta.delivery.payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    // 가맹점 id로 결제내역 찾기 - 가장 최근의 결제를 가져옵니다.
    List<Payment> findTopByUserIdOrderByCreatedAtDesc(Long user_id);

    // 특정 사용자에 대한 결제 내역을 페이징된 결과로 반환합니다.
    Page<Payment> findAll(Pageable pageable);

    Optional<Payment> findById(UUID id);

    // 특정 사용자에 대한 결제 내역을 페이징된 결과로 반환합니다.
    Page<Payment> findAllByUserId(Long userId, Pageable pageable);

}


package com.sparta.delivery.order;

import com.sparta.delivery.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUser(User user);

    List<Order> findByUserId(Long id);

    Page<Order> findByStatus(OrderStatusEnum status, Pageable pageable);
}


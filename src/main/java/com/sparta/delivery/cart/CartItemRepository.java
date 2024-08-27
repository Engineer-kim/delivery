package com.sparta.delivery.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    CartItem findByCartIdAndProductProductId(UUID id, UUID productId);

    List<CartItem> findByCart(Cart cart);
}

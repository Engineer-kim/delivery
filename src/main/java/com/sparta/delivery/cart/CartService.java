package com.sparta.delivery.cart;

import com.sparta.delivery.cart.dto.CartItemDto;
import com.sparta.delivery.cart.dto.CartResponseDto;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.product.ProductRepository;
import com.sparta.delivery.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    // 장바구니에 상품 추가 (상품 추가시 장바구니 생성)
    @Transactional
    public UUID addCartItem(User user, CartItemDto cartItemDto) {
        Product product = productRepository.findById(cartItemDto.getProductId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다.")
        );

        // 장바구니가 없는 유저면 장바구니 생성
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = Cart.createCart(user);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndProductProductId(cart.getId(), product.getProductId());

        if (savedCartItem != null) {
            // 장바구니에 이미 존재하는 상품이면 개수만 추가
            savedCartItem.addQuantity(cartItemDto.getQuantity());
            cartItemRepository.save(savedCartItem);
            return savedCartItem.getId();
        } else {
            // 장바구니에 없는 상품이면 담기
            CartItem cartItem = CartItem.createCartItem(cart, product, cartItemDto.getQuantity());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }


    }

    // 장바구니 상품 조회
    @Transactional(readOnly = true)
    public List<CartResponseDto> getCartItems(User user) {
        List<CartResponseDto> cartResponseDtoList = new ArrayList<>();
        Cart cart = cartRepository.findByUserId(user.getId());

        // 장바구니가 없으면 빈 리스트 반환
        if (cart == null) {
            return cartResponseDtoList;
        }

        // 장바구니에 있는 상품들 dto 로 변환
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        cartResponseDtoList = cartItems.stream()
                .map(this::mapToCartResponseDto)
                .collect(Collectors.toList());

        return cartResponseDtoList;
    }

    // 장바구니 아이템 수량 변경
    @Transactional
    public void updateCartItemQuantity(UUID cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 상품입니다.")
                );

        cartItem.updateQuantity(quantity);
    }

    // 장바구니 아이템 삭제
    @Transactional
    public void deleteCartItem(UUID cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 상품입니다.")
                );
        cartItemRepository.delete(cartItem);
    }




    // CartItem 을 CartResponseDto 로 매핑하는 메서드
    public CartResponseDto mapToCartResponseDto(CartItem cartItem) {
        return new CartResponseDto(
                cartItem.getId(),
                cartItem.getProduct().getProductId(),
                cartItem.getProduct().getProductName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity()
        );
    }
}

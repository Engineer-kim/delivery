package com.sparta.delivery.cart;

import com.sparta.delivery.cart.dto.CartItemDto;
import com.sparta.delivery.cart.dto.CartResponseDto;
import com.sparta.delivery.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    // 장바구니 생성
    @PostMapping
    public void addCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartItemDto cartItemDto) {
        UUID cartId = cartService.addCartItem(userDetails.getUser(), cartItemDto);
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<CartResponseDto> cartResponseDtos = cartService.getCartItems(userDetails.getUser());
        return ResponseEntity.ok(cartResponseDtos);
    }



}

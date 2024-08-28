package com.sparta.delivery.cart;

import com.sparta.delivery.cart.dto.CartItemDto;
import com.sparta.delivery.cart.dto.CartItemUpdateDto;
import com.sparta.delivery.cart.dto.CartResponseDto;
import com.sparta.delivery.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public UUID addCartItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CartItemDto cartItemDto) {
        return cartService.addCartItem(userDetails.getUser(), cartItemDto);
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCartItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<CartResponseDto> cartResponseDtos = cartService.getCartItems(userDetails.getUser());
        return ResponseEntity.ok(cartResponseDtos);
    }

    // 장바구니 아이템 수량 변경
    @PatchMapping
    public ResponseEntity<String> updateCartAmount(@RequestBody CartItemUpdateDto cartItemDto) {
        cartService.updateCartItemQuantity(cartItemDto.getCartItemId(), cartItemDto.getQuantity());
        return ResponseEntity.ok("CartItem updated successfully");
    }

    // 장바구니 아이템 삭제
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable UUID cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok("CartItem deleted successfully");
    }


    // 장바구니 비우기
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        cartService.clearCart(userDetails.getUser());
        return new ResponseEntity<>("장바구니가 비워졌습니다.", HttpStatus.OK);
    }


}

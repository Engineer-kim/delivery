package com.sparta.delivery.order;

import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import com.sparta.delivery.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody OrderRequestDto requestDto
    ) {
        UUID orderId = orderService.createOrder(userDetails.getUser(),requestDto);
        OrderResponseDto responseDto = orderService.addOrderItems(orderId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable UUID orderId) {
        OrderResponseDto responseDto = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(responseDto);
    }
}

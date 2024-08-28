package com.sparta.delivery.order;

import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import com.sparta.delivery.order.dto.OrderStatusUpdateDto;
import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    // 주문 생성
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

    // 본인 주문 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<OrderResponseDto> responseDtoList = orderService.getAllOrdersForUser(userDetails.getUser());
        return ResponseEntity.ok(responseDtoList);
    }

    // 본인 주문 취소
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User currentUser = userDetails.getUser();
        OrderResponseDto responseDto = orderService.cancelOrder(orderId, currentUser);
        return ResponseEntity.ok(responseDto);
    }



    // 주문 상태 수정 (가게 주인)
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.OWNER})
    @PutMapping("/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@RequestBody OrderStatusUpdateDto statusUpdateDto) {
        OrderResponseDto responseDto = orderService.updateOrderStatus(statusUpdateDto);
        return ResponseEntity.ok(responseDto);
    }



    // MASTER
    // 관리자의 전체 주문 조회
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    @GetMapping("/admin/all")
    public List<OrderResponseDto> getAllOrdersForAdmin() {
        return orderService.getAllOrdersForAdmin();
    }

}

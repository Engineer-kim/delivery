package com.sparta.delivery.payment;

import com.sparta.delivery.common.ApiResponse;
import com.sparta.delivery.order.Order;
import com.sparta.delivery.order.OrderRepository;
import com.sparta.delivery.security.UserDetailsImpl;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    //결제 요청
    @PostMapping
    public ResponseEntity<ApiResponse> createPayment(@RequestParam UUID orderId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("주문 id를 찾을 수 없습니다."));
        // 사용자의 ID를 가져와서 결제 정보를 설정합니다.
        Map<String, Object> createdPayment = paymentService.createPayment(order, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(
            201, "success", "결제 요청이 성공적으로 처리되었습니다.", createdPayment));
    }

    //결제승인
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse> approvePayment(@RequestParam String pgToken,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Map<String, Object> approvedPayment = paymentService.approvedPayment(pgToken, userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(
            200, "success", "결제 승인이 성공적으로 처리되었습니다.", approvedPayment
        ));
    }

    //payment id로 결제 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentByPaymentId(@PathVariable UUID id) {
        Payment payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(payment);
    }

    //payment user id로 결제 정보 조회
    @GetMapping("/users/{userId}")
    public Page<Payment> getPaymentSearchByUserId(
        @PathVariable Long userId,
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(required = false) String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size,
            sortBy == null ? Sort.by("createdAt").descending() : Sort.by(sortBy));

        Page<Payment> payment = paymentService.getPaymentAllSearchByUserId(userId,pageable);

        return payment;
    }

    //결제 page 반환(list)
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getAllPayments(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam(required = false) String sortBy) {

        Pageable pageable = PageRequest.of(page, size,
            sortBy == null ? Sort.by("createdAt").descending() : Sort.by(sortBy));

        Page<Payment> paymentPage = paymentService.getPaymentsAll(pageable);
        return ResponseEntity.ok(new ApiResponse(
            200, "success", "사용자 결제 내역 조회 성공", paymentPage));
    }

    //결제 내역 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);

        return ResponseEntity.ok(new ApiResponse(
            200, "success", "결제가 성공적으로 삭제되었습니다.", null));
    }
}


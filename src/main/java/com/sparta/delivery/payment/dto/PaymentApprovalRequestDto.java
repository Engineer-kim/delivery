package com.sparta.delivery.payment.dto;

import lombok.Builder;

@Builder
public record PaymentApprovalRequestDto(
    String cid,
    String tid,
    String partnerOrderId,
    String partnerUserId,
    String pgToken, // 결제승인 요청을 인증하는 토큰
    Integer totalAmount
) {
}

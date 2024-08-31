package com.sparta.delivery.payment.dto;

import java.sql.Time;
import java.time.LocalDateTime;

public record PaymentReqResponseDto(
    String tid,
    boolean tms_result,
    LocalDateTime created_at,
    String  next_redirect_pc_url
) {

}

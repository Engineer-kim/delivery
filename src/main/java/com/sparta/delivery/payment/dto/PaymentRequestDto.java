package com.sparta.delivery.payment.dto;

public record PaymentRequestDto(
    String cid,
    String partner_order_id,
    String payment_method_type,
    String partner_user_id,
    String item_name,
    String quantity,
    Long total_amount,
    Long tax_free_amount,
    String approval_url,
    String fail_url,
    String cancel_url
) {

}

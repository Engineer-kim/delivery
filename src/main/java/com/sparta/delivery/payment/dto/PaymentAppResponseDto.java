package com.sparta.delivery.payment.dto;

import lombok.Builder;

@Builder
public record PaymentAppResponseDto(
    String aid,
    String tid,
    String cid,
    String sid,
    String partnerOrderId,
    String partnerUserId,
    String itemName,
    String itemCode,
    String payload,
    int quantity,
    Amount amount,
    String paymentMethodType,
    CardInfo cardInfo,
    String sequentialPaymentMethods,
    String createdAt,
    String approvedAt
) {

    // Nested record classes for Amount and CardInfo
    public record Amount(
        int total,
        int taxFree,
        int vat,
        int point,
        int discount,
        int greenDeposit
    ) {

    }

    public record CardInfo(
        String kakaopayPurchaseCorp,
        String kakaopayPurchaseCorpCode,
        String kakaopayIssuerCorp,
        String kakaopayIssuerCorpCode,
        String bin,
        String cardType,
        String installMonth,
        String approvedId,
        String cardMid,
        String interestFreeInstall,
        String cardItemCode,
        String installmentType
    ) {

    }
}


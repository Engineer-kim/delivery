package com.sparta.delivery.payment;


import com.sparta.delivery.order.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "p_payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;  // DB에서 자동으로 생성되는 고유 ID

    @Column(name = "aid", unique = true)
    private String aid;

    @Column(name = "tid", unique = true)
    private String tid;

    @Column(name = "cid")
    private String cid;//가맹점 코드

    @Column(name = "partner_order_id")
    private String partnerOrderId;//가맹점 주문 번호

    @Column(name = "partner_user_id")
    private String partnerUserId;//가맹점 회원 id

    @Column(name = "item_name")
    private String itemName; // 상품명을 넣어야 되는데 리스트 형식으로 되어 있으므로 가게 이름을 넣겠음

    @Column(name = "quantity")
    private Integer quantity; // 상품 수량

    @Column
    private Integer totalAmount;// 상품 총액

    @Column
    private Integer taxFreeAmount;// 상품 비과세 금액

    @Column
    private boolean isDeleted;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private String deletedBy;

    @Embedded
    private Amount amount;

    @Column(name = "payment_method_type")
    private String paymentMethodType;//지불 방법 현재는 카드만 됨

    @Embedded
    private CardInfo cardInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    // Getters and Setters
    @Getter
    @Setter
    // Embedded classes for Amount and CardInfo
    @Embeddable
    public static class Amount {

        @Column(name = "total")
        private int total;

        @Column(name = "tax_free")
        private int taxFree;

        @Column(name = "vat")
        private int vat;

        @Column(name = "point")
        private int point;

        @Column(name = "discount")
        private int discount;

        @Column(name = "green_deposit")
        private int greenDeposit;

    }

    @Getter
    @Setter
    @Embeddable
    public static class CardInfo {

        @Column(name = "kakaopay_purchase_corp")
        private String kakaopayPurchaseCorp;

        @Column(name = "kakaopay_purchase_corp_code")
        private String kakaopayPurchaseCorpCode;

        @Column(name = "kakaopay_issuer_corp")
        private String kakaopayIssuerCorp;

        @Column(name = "kakaopay_issuer_corp_code")
        private String kakaopayIssuerCorpCode;

        @Column(name = "card_type")
        private String cardType;

        // Getters and Setters
    }
}



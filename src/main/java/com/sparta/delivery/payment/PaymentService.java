package com.sparta.delivery.payment;

import static com.sparta.delivery.payment.PaymentEnum.CARD;

import com.sparta.delivery.order.Order;
import com.sparta.delivery.order.OrderRepository;
import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.admin-key}")
    private String adminKey;

    @Value("${kakaopay.approval-url}")
    private String approvalUrl;//결제승인시 리다이렉트 되는 페이지

    @Value("${kakaopay.fail-url}")
    private String failUrl;

    @Value("${kakaopay.cancel-url}")
    private String cancelUrl;

    @Value("${kakao-url.request}")
    private String requestUrl;

    @Value("${kakao-url.approve}")
    private String approveUrl;//결제 승인 요청할때

    private final String AUTHORIZATION_HEADER = "Authorization";

    private final Integer QUANTITY = 1;

    private final Integer TAX_FREE_AMOUNT = 0;

    // 결제 생성 메소드
    public Map<String, Object> createPayment(Order order, UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("user id를 찾을 수 없습니다."));

        // 결제 요청을 위한 데이터 구성
        Map<String, Object> requestBody = new ConcurrentHashMap<>();
        requestBody.put("cid", cid);
        requestBody.put("partner_order_id", String.valueOf(order.getStore().getShopId()));
        requestBody.put("partner_user_id", String.valueOf(order.getStore().getUserId()));
        requestBody.put("item_name", order.getStore().getShopName());
        requestBody.put("quantity", QUANTITY);  // 예시 값, 실제 데이터로 변경 필요
        requestBody.put("total_amount", order.getTotalAmount());  // 예시 값, 실제 데이터로 변경 필요
        requestBody.put("tax_free_amount", TAX_FREE_AMOUNT);
        requestBody.put("payment_method_type", CARD.name());
        requestBody.put("approval_url", approvalUrl);
        requestBody.put("fail_url", failUrl);
        requestBody.put("cancel_url", cancelUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION_HEADER, "SECRET_KEY " + adminKey);  // 카카오페이의 인증 헤더 설정

        // HTTP 요청 엔티티 구성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = requestUrl;
        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            Map.class
        );

        // 응답 데이터 처리
        Map<String, Object> responseBody = response.getBody();
        String tid = (String) responseBody.get("tid");
        String createdAt = (String) responseBody.get("created_at");

        Payment payment = Payment.builder()
            .cid(cid)
            .partnerUserId(String.valueOf(order.getStore().getUserId()))//가게 사장님 id
            .partnerOrderId(String.valueOf(order.getStore().getShopId()))//가게 id
            .itemName(order.getStore().getShopName())
            .totalAmount(order.getTotalAmount())
            .paymentMethodType(CARD.name())
            .tid(tid)
            .createdAt(LocalDateTime.parse(createdAt))
            .quantity(QUANTITY)
            .taxFreeAmount(TAX_FREE_AMOUNT)
            .isDeleted(false)
            .order(order)
            .user(user)
            .build();
        paymentRepository.save(payment);

        return responseBody;
    }

    // 결제 ID로 조회 메소드
    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(String.valueOf(paymentId))
            .orElseThrow(() -> new RuntimeException("결제를 찾을 수 없습니다."));
    }

    // 특정 사용자에 대한 결제 내역 조회 (페이징 포함)
//    public Page<Payment> getPaymentsByUserId(UUID userId, Pageable pageable) {
//        return paymentRepository.findByUserId(userId, pageable);
//    }

    // 결제 삭제 (논리 삭제)
    public void deletePayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setDeleted(true);
        payment.setDeletedAt(LocalDateTime.now());
        payment.setDeletedBy("관리자"); // 현재 사용자의 ID나 이름으로 설정
        paymentRepository.save(payment);
    }

    public Map<String, Object> approvedPayment(String pgToken, UserDetailsImpl userDetails) {
        List<Payment> payments = paymentRepository.findTopByUserIdOrderByCreatedAtDesc(
            userDetails.getUser().getId());

        Payment payment;
        if (!payments.isEmpty()) {
            payment = payments.get(0); // 가장 최근 결제 내역 반환
        } else {
            throw new RuntimeException("해당 가맹점 사용자 ID로 결제 내역을 찾을 수 없습니다.");
        }

        // 요청 본문을 Map으로 구성
        Map<String, Object> requestBody = new ConcurrentHashMap<>();
        requestBody.put("cid", payment.getCid()); // 결제 CID 값을 설정합니다.
        requestBody.put("tid", payment.getTid()); // 결제 TID 값을 설정합니다.
        requestBody.put("partner_order_id", payment.getPartnerOrderId()); // 가맹점 주문 ID 값을 설정합니다.
        requestBody.put("partner_user_id", payment.getPartnerUserId()); // 가맹점 사용자 ID 값을 설정합니다.
        requestBody.put("pg_token", pgToken); // 결제 승인 요청을 인증하는 토큰을 설정합니다.
        requestBody.put("total_amount", payment.getTotalAmount()); // 상품 총액을 설정합니다.

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION_HEADER, "SECRET_KEY " + adminKey);  // 카카오페이의 인증 헤더 설정

        // HTTP 요청 엔티티 구성
        // HTTP 요청 엔티티 구성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            approveUrl,
            HttpMethod.POST,
            entity,
            Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("결제 승인 응답이 비어 있습니다.");
        }

        // Map to Payment entity
        payment.setAid((String) responseBody.get("aid"));
        payment.setTid((String) responseBody.get("tid"));
        payment.setCid((String) responseBody.get("cid"));
        payment.setPartnerOrderId((String) responseBody.get("partner_order_id"));
        payment.setPartnerUserId((String) responseBody.get("partner_user_id"));
        payment.setItemName((String) responseBody.get("item_name"));
        payment.setQuantity((Integer) responseBody.get("quantity"));
        payment.setPaymentMethodType((String) responseBody.get("payment_method_type"));
        payment.setCreatedAt(LocalDateTime.parse((String) responseBody.get("created_at")));
        payment.setApprovedAt(LocalDateTime.parse((String) responseBody.get("approved_at")));

        // Extract Amount information
        Map<String, Object> amountMap = (Map<String, Object>) responseBody.get("amount");
        if (amountMap != null) {
            Payment.Amount amount = new Payment.Amount();
            amount.setTotal((Integer) amountMap.get("total"));
            amount.setTaxFree((Integer) amountMap.get("tax_free"));
            amount.setVat((Integer) amountMap.get("vat"));
            amount.setPoint((Integer) amountMap.get("point"));
            amount.setDiscount((Integer) amountMap.get("discount"));
            amount.setGreenDeposit((Integer) amountMap.get("green_deposit"));
            payment.setAmount(amount);
        }

        // Extract CardInfo information if available
        Map<String, Object> cardInfoMap = (Map<String, Object>) responseBody.get("card_info");
        if (cardInfoMap != null) {
            Payment.CardInfo cardInfo = new Payment.CardInfo();
            cardInfo.setKakaopayPurchaseCorp((String) cardInfoMap.get("kakaopay_purchase_corp"));
            cardInfo.setKakaopayPurchaseCorpCode(
                (String) cardInfoMap.get("kakaopay_purchase_corp_code"));
            cardInfo.setKakaopayIssuerCorp((String) cardInfoMap.get("kakaopay_issuer_corp"));
            cardInfo.setKakaopayIssuerCorpCode(
                (String) cardInfoMap.get("kakaopay_issuer_corp_code"));
            cardInfo.setCardType((String) cardInfoMap.get("card_type"));
            payment.setCardInfo(cardInfo);
        }

        paymentRepository.save(payment);

        // 응답 본문을 반환
        return response.getBody();
    }

}


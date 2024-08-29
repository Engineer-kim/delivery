package com.sparta.delivery.order;

import com.sparta.delivery.address.UserAddress;
import com.sparta.delivery.address.UserAddressRepository;
import com.sparta.delivery.cart.Cart;
import com.sparta.delivery.cart.CartItem;
import com.sparta.delivery.cart.CartItemRepository;
import com.sparta.delivery.cart.CartRepository;
import com.sparta.delivery.order.dto.*;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.repo.ShopRepository;
import com.sparta.delivery.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShopRepository shopRepository;
    private final UserAddressRepository userAddressRepository;


    // 주문 생성
    @Transactional
    public UUID createOrder(User user, OrderRequestDto requestDto) {

        Store store = shopRepository.findById(requestDto.getShopId())
                .orElseThrow(() -> new IllegalArgumentException("상점을 찾을 수 없습니다."));

        UserAddress address = userAddressRepository.findById(requestDto.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));
        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDING);
        order.setUser(user);
        order.setStore(store);
        order.setType(requestDto.getTypeEnum());
        order.setRequest(requestDto.getRequest());
        order.setAddress(address);
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }


    // 장바구니에 들어있는 상품들 주문 처리
    @Transactional
    public OrderResponseDto addOrderItems(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUserId(order.getUser().getId());
        if (cart == null) {
            throw new IllegalArgumentException("장바구니가 비어있습니다.");
        }

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어있습니다.");
        }

        int totalAmount = 0;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProduct().getProductId());
            orderItem.setProductName(cartItem.getProduct().getProductName());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            order.addOrderItem(orderItem);

            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);


        // 장바구니 비우기
        cartItemRepository.deleteAll(cartItems);
        cartRepository.delete(cart);

        orderRepository.save(order);

        return toOrderResponseDto(order);

    }



    // 주문 단건 조회
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderDetails(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return toOrderResponseDto(order);
    }


    // 전체 주문 조회 (MASTER)
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(OrderService::toOrderResponseDto)
                .collect(Collectors.toList());
    }

    // 사용자별 전체 주문 조회 (본인 주문만)
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrdersForUser(User user) {
        List<Order> orders = orderRepository.findByUserId(user.getId());

        return orders.stream()
                .map(OrderService::toOrderResponseDto)
                .collect(Collectors.toList());
    }

    // 가게별 전체 주문 조회 (가게 주인)
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrdersByStore(UUID shopId) {
        List<Order> orders = orderRepository.findByStore_ShopId(shopId);

        return orders.stream()
                .map(OrderService::toOrderResponseDto)
                .collect(Collectors.toList());
    }


    // 주문 상태 수정 (가게 주인)
    @Transactional
    public OrderResponseDto updateOrderStatus(OrderStatusUpdateDto statusUpdateDto) {
        Order order = orderRepository.findById(statusUpdateDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + statusUpdateDto.getOrderId()));

        order.setStatus(statusUpdateDto.getNewStatus());
        orderRepository.save(order);

        return OrderService.toOrderResponseDto(order);
    }

    // 주문 취소
    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 주문의 소유자 확인
        if (!order.getUser().equals(currentUser)) {
            throw new IllegalStateException("본인 주문만 취소할 수 있습니다.");
        }

        // 주문이 이미 완료되었거나 취소된 경우
        if (order.getStatus() == OrderStatusEnum.COMPLETED || order.getStatus() == OrderStatusEnum.CANCELED) {
            throw new IllegalStateException("이미 완료되었거나 취소된 주문입니다.");
        }

        // 주문 상태를 취소로 변경
        order.setStatus(OrderStatusEnum.CANCELED);
        orderRepository.save(order);

        return toOrderResponseDto(order);
    }

    // 상점 Status 로 주문 검색
    public Page<OrderResponseDto> getOrdersByStatus(OrderStatusEnum status, int page, int size, String sort) {
        // 페이지 크기 기본값
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 정렬 방식 기본값
        Sort sortOrder = Sort.by(Sort.Direction.ASC, "createdAt"); // 기본값: 생성일 순
        if ("modifiedAt".equalsIgnoreCase(sort)) {
            sortOrder = Sort.by(Sort.Direction.ASC, "modifiedAt"); // 수정일 순
        }

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Order> orders = orderRepository.findByStatus(status, pageable);

        // Page<Order>를 Page<OrderResponseDto>로 변환
        return orders.map(OrderService::toOrderResponseDto);
    }


    // order -> dto 매퍼
    public static OrderResponseDto toOrderResponseDto(Order order) {
        Store store = order.getStore(); // 상점 정보 가져오기
        OrderResponseDto dto = OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId()) // userId는 User 엔티티에서 가져옵니다
                .shopId(store.getShopId()) // 상점 ID
                .shopName(store.getShopName()) // 상점 이름 추가
                .typeEnum(order.getType())
                .statusEnum(order.getStatus())
                .request(order.getRequest())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt()) // 주문 일자 추가
                .addressLine1(order.getAddress().getLine1()) // 주소 Line1 추가
                .addressLine2(order.getAddress().getLine2()) // 주소 Line2 추가
                .orderItems(order.getOrderItems().stream()
                        .map(item -> new OrderItemDto(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getPrice()))
                        .collect(Collectors.toList()))
                .build();

        return dto;
    }


}

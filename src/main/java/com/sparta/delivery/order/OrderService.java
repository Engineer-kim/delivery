package com.sparta.delivery.order;

import com.sparta.delivery.cart.Cart;
import com.sparta.delivery.cart.CartItem;
import com.sparta.delivery.cart.CartItemRepository;
import com.sparta.delivery.cart.CartRepository;
import com.sparta.delivery.order.dto.OrderItemDto;
import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import com.sparta.delivery.order.dto.OrderStatusUpdateDto;
import com.sparta.delivery.order.orderitem.OrderItemRepository;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.product.ProductRepository;
import com.sparta.delivery.product.ProductService;
import com.sparta.delivery.shop.entity.Store;
import com.sparta.delivery.shop.repo.ShopRepository;
import com.sparta.delivery.user.User;
import com.sparta.delivery.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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


    // 주문 생성
    @Transactional
    public UUID createOrder(User user, OrderRequestDto requestDto) {

        Store store = shopRepository.findById(requestDto.getShopId())
                .orElseThrow(() -> new IllegalArgumentException("상점을 찾을 수 없습니다."));


        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDING);
        order.setUser(user);
        order.setStore(store);
        order.setType(requestDto.getTypeEnum());
        order.setRequest(requestDto.getRequest());

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


    private OrderItem createOrderItem(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(cartItem.getProduct().getProductId());
        orderItem.setProductName(cartItem.getProduct().getProductName());
        orderItem.setPrice(cartItem.getProduct().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        return orderItem;
    }


    // order -> dto 매퍼
    public static OrderResponseDto toOrderResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setShopId(order.getStore().getShopId());
        dto.setShopName(order.getStore().getShopName());
        dto.setUserId(order.getUser().getId()); // userId는 User 엔티티에서 가져옵니다
        dto.setTypeEnum(order.getType());
        dto.setStatusEnum(order.getStatus());
        dto.setRequest(order.getRequest());
        dto.setTotalAmount(order.getTotalAmount());
        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice()))
                .collect(Collectors.toList());

        dto.setOrderItems(orderItemDtos);
        return dto;
    }


}

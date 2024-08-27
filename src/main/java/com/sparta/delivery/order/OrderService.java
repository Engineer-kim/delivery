package com.sparta.delivery.order;

import com.sparta.delivery.cart.Cart;
import com.sparta.delivery.cart.CartItem;
import com.sparta.delivery.cart.CartItemRepository;
import com.sparta.delivery.cart.CartRepository;
import com.sparta.delivery.order.dto.OrderRequestDto;
import com.sparta.delivery.order.dto.OrderResponseDto;
import com.sparta.delivery.order.orderitem.OrderItemRepository;
import com.sparta.delivery.product.Product;
import com.sparta.delivery.product.ProductRepository;
import com.sparta.delivery.product.ProductService;
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
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public UUID createOrder(User user, OrderRequestDto requestDto) {

        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDING);
        order.setUser(user);
        order.setType(requestDto.getTypeEnum());
        order.setRequest(requestDto.getRequest());

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }


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

        // Add new OrderItems to the existing Order
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProduct().getProductId());
            orderItem.setProductName(cartItem.getProduct().getProductName());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            // Add to Order using addOrderItem method
            order.addOrderItem(orderItem);

            // Calculate total amount
            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        // Save Order and OrderItems
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


    public static OrderResponseDto toOrderResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUser().getId()); // userId는 User 엔티티에서 가져옵니다
        dto.setTypeEnum(order.getType());
        dto.setStatusEnum(order.getStatus());
        dto.setRequest(order.getRequest());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderItems(order.getOrderItems());
        return dto;
    }

}

package com.sparta.delivery.order;

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

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;


    public OrderResponseDto createOrder(User user, OrderRequestDto requestDto) {
        Order order = new Order();
        order.setStatus(OrderStatusEnum.PRE_ORDER);
        order.setUser(user);
        order.setType(requestDto.getTypeEnum());
        order.setRequest(requestDto.getRequest());
        orderRepository.save(order);
        return toOrderResponseDto(order);
    }



    public static OrderResponseDto toOrderResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUser().getId()); // userId는 User 엔티티에서 가져옵니다
        dto.setTypeEnum(order.getType());
        dto.setStatusEnum(order.getStatus());
        dto.setRequest(order.getRequest());
        dto.setTotalAmount(order.getTotalAmount());
        List<OrderItem> orderItems = order.getOrderItems(); // OrderItem 리스트를 직접 사용
        dto.setOrderItems(orderItems);
        return dto;
    }



}

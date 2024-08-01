package com.example.spring.boot.mapper;

import com.example.spring.boot.dto.OrderRequestDto;
import com.example.spring.boot.dto.OrderResponseDto;
import com.example.spring.boot.model.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order mapToOrder(OrderRequestDto orderRequestDto) {
        return Order.builder()
                .productId(orderRequestDto.getProductId())
                .inventoryId(orderRequestDto.getInventoryId())
                .orderStatus(orderRequestDto.getOrderStatus())
                .quantity(orderRequestDto.getQuantity())
                .orderDate(LocalDateTime.now())
                .shippingAddress(orderRequestDto.getShippingAddress())
                .build();
    }

    public Order mapToOrder(OrderResponseDto orderResponseDto) {
        return Order.builder()
                .id(orderResponseDto.getId())
                .productId(orderResponseDto.getProductId())
                .inventoryId(orderResponseDto.getInventoryId())
                .orderStatus(orderResponseDto.getOrderStatus())
                .quantity(orderResponseDto.getQuantity())
                .orderDate(LocalDateTime.now())
                .shippingAddress(orderResponseDto.getShippingAddress())
                .build();
    }

    public OrderResponseDto mapToOrderResponseDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .inventoryId(order.getInventoryId())
                .orderStatus(order.getOrderStatus())
                .quantity(order.getQuantity())
                .orderDate(LocalDateTime.now())
                .shippingAddress(order.getShippingAddress())
                .build();
    }

    public List<OrderResponseDto> mapToOrderResponseDtoList(List<Order> orderList) {
        return orderList.stream()
                .map(this::mapToOrderResponseDto)
                .collect(Collectors.toList());
    }
}

package com.example.spring.boot.mapper;

import com.example.spring.boot.dto.OrderRequestDto;
import com.example.spring.boot.dto.OrderResponseDto;
import com.example.spring.boot.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order mapToOrder(OrderRequestDto orderRequestDto);

    Order mapToOrder(OrderResponseDto orderResponseDto);

    OrderResponseDto mapToOrderResponseDto(Order order);

    List<OrderResponseDto> mapToOrderResponseDtoList(List<Order> orderList);
}

package com.example.payment_service.external;

import com.example.payment_service.dto.orderDto.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderServiceClient {

    @GetMapping("api/v1/orders/{id}")
    OrderResponseDto getOrderById(@PathVariable String id);
}

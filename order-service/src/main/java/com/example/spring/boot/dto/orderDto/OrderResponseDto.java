package com.example.spring.boot.dto.orderDto;

import com.example.spring.boot.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto implements Serializable {

    private String id;
    private String productId;
    private String inventoryId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private int quantity;
    private double totalAmount;
    private String shippingAddress;
}

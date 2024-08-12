package com.example.payment_service.dto.orderDto;

import com.example.payment_service.model.Address;
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
    private String orderStatus;
    private int quantity;
    private double totalAmount;
    private Address shippingAddress;
}

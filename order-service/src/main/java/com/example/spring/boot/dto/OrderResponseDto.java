package com.example.spring.boot.dto;

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
    private LocalDateTime orderDate; // Date and time when the order was placed
    private String orderStatus; // Current status of the order
    private int quantity;
    private double totalAmount; // Total amount for the order
    private String shippingAddress; // Address where the order will be shipped
}

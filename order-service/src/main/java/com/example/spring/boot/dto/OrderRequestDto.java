package com.example.spring.boot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class OrderRequestDto implements Serializable {

    @NotEmpty(message = "Order productId cannot be empty")
    private String productId;

    @NotEmpty(message = "Order inventoryId cannot be empty")
    private String inventoryId;

    private LocalDateTime orderDate; // Date and time when the order was placed

    @NotEmpty(message = "Order orderStatus cannot be empty")
    private String orderStatus; // Current status of the order

    @NotNull(message = "Order quantity cannot be null")
    @Min(value = 0,message = "Order quantity must be greater than zero")
    private int quantity;
    private double totalAmount; // Total amount for the order

    @NotEmpty(message = "Order shippingAddress cannot be null")
    private String shippingAddress; // Address where the order will be shipped
}

package com.example.spring.boot.dto.orderDto;

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

    private LocalDateTime orderDate;

    @NotNull(message = "Order quantity cannot be null")
    @Min(value = 0, message = "Order quantity must be greater than zero")
    private int quantity;

    @NotEmpty(message = "Order shippingAddress cannot be null")
    private String shippingAddress;
}

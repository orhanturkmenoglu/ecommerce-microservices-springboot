package com.example.spring.boot.dto.orderDto;

import com.example.spring.boot.model.Address;
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
public class OrderUpdateRequestDto implements Serializable {


    @NotEmpty(message = "Order customer Id cannot be empty")
    private String customerId;

    @NotEmpty(message = "Order productId cannot be empty")
    private String productId;

    @NotEmpty(message = "Order inventoryId cannot be empty")
    private String inventoryId;

    @NotEmpty(message = "Order paymentId cannot be empty")
    private String paymentId;

    private LocalDateTime orderDate;

    @NotNull(message = "Order quantity cannot be null")
    @Min(value = 0, message = "Order quantity must be greater than zero")
    private int quantity;

    private Address shippingAddress;
}

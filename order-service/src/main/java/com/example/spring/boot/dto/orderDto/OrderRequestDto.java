package com.example.spring.boot.dto.orderDto;

import com.example.spring.boot.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for creating a new order")
public class OrderRequestDto implements Serializable {

    @Schema(description = "Unique identifier for the customer placing the order", example = "customer123")
    @NotEmpty(message = "Order customer Id cannot be empty")
    private String customerId;

    @Schema(description = "Unique identifier for the product being ordered", example = "product456")
    @NotEmpty(message = "Order productId cannot be empty")
    private String productId;

    @Schema(description = "Unique identifier for the inventory related to the order", example = "inventory789")
    @NotEmpty(message = "Order inventoryId cannot be empty")
    private String inventoryId;

    @Schema(description = "Quantity of the product being ordered", example = "10")
    @NotNull(message = "Order quantity cannot be null")
    @Min(value = 1, message = "Order quantity must be greater than zero")
    private int quantity;

    @Schema(description = "Shipping address for the order")
    private Address shippingAddress;
}

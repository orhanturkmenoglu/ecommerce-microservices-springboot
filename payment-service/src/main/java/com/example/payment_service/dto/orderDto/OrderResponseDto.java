package com.example.payment_service.dto.orderDto;

import com.example.payment_service.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for order response")
public class OrderResponseDto implements Serializable {

    @Schema(description = "Unique identifier of the order", example = "order123")
    private String id;

    @Schema(description = "Unique identifier of the product associated with the order", example = "prod456")
    private String productId;

    @Schema(description = "Unique identifier of the inventory associated with the order", example = "inv789")
    private String inventoryId;

    @Schema(description = "Date and time when the order was placed", example = "2024-08-30T15:00:00")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order", example = "SHIPPED")
    private String orderStatus;

    @Schema(description = "Quantity of the product ordered", example = "3")
    private int quantity;

    @Schema(description = "Total amount for the order", example = "299.99")
    private double totalAmount;

    @Schema(description = "Shipping address for the order")
    private Address shippingAddress;
}

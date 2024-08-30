package com.example.payment_service.model;

import com.example.payment_service.enums.CargoStatus;
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
public class Cargo implements Serializable {

    private String id;

    private String orderId;

    private String customerId;

    private String trackingNumber;  // izleme numarası

    private CargoStatus status;

    private LocalDateTime lastUpdated;
}

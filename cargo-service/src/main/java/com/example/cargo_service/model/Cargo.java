package com.example.cargo_service.model;

import com.example.cargo_service.enums.CargoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cargo implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    private String id;

    private String orderId;

    private String customerId;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private CargoStatus status;

    private LocalDateTime lastUpdated;

}

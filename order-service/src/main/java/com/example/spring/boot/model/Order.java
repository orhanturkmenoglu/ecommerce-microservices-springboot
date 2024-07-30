package com.example.spring.boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    private String id;
    private String productId;
    private String inventoryId;
    private LocalDateTime orderDate; // Date and time when the order was placed
    private String orderStatus; // Current status of the order
    private int quantity;
    private double totalAmount; // Total amount for the order
    private String shippingAddress; // Address where the order will be shipped

}

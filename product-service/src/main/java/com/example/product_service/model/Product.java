package com.example.product_service.model;

import com.example.product_service.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    private String id;

    private String inventoryId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category = Category.DİĞER;

    private double price;

    private LocalDateTime createdDate;

    private Inventory inventory;

}

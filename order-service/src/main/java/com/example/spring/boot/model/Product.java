package com.example.spring.boot.model;

import com.example.spring.boot.enums.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product implements Serializable {

    private String id;
    private String inventoryId;
    private String name;
    private String description;
    private Category category;
    private double price;
}

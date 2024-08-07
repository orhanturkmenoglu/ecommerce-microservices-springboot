package com.example.spring.boot.model;

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
    private String category;
    private double price;
}

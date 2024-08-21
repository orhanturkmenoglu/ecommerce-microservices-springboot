package com.example.product_service.repository;

import com.example.product_service.enums.Category;
import com.example.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByInventoryId(String inventoryId);

    List<Product> findByCategory(Category category);
}
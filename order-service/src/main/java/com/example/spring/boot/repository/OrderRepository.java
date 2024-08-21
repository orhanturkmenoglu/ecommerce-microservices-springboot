package com.example.spring.boot.repository;

import com.example.spring.boot.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByProductId(String productId);

    Optional<Order> findByInventoryId(String inventoryId);

    List<Order> findByOrderDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}

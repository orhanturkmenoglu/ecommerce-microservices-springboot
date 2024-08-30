package com.example.cargo_service.repository;

import com.example.cargo_service.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, String> {

    Optional<Cargo> findByOrderId(String orderId);

    Optional<Cargo> findCargoByTrackingNumber(String trackingNumber);
}
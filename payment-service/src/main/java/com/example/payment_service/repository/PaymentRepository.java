package com.example.payment_service.repository;

import com.example.payment_service.enums.PaymentType;
import com.example.payment_service.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByCustomerId(String customerId);

    List<Payment> findByPaymentType(PaymentType paymentType);

    List<Payment> findByPaymentDateBetween(LocalDateTime startDate,LocalDateTime endDate);


}

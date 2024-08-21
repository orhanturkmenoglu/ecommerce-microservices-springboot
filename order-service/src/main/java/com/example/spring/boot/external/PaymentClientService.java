package com.example.spring.boot.external;

import com.example.spring.boot.dto.paymentDto.PaymentUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClientService {

    @PostMapping("/api/v1/payments/cancel/{paymentId}/")
    void cancelPayment(@PathVariable String paymentId);

    @GetMapping("/api/v1/payments/{paymentId}")
    void getPaymentById(@PathVariable("paymentId") String paymentId);

    @GetMapping("/api/v1/payments/paymentCustomer/{customerId}")
    void getPaymentCustomerById(@PathVariable("customerId") String customerId);

    @PutMapping("/api/v1/payments")
    void updatePayment(@RequestBody PaymentUpdateRequestDto paymentUpdateRequestDto);

    @DeleteMapping("/api/v1/payments/{paymentId}")
    void deletePaymentById(@PathVariable("paymentId") String paymentId);
}

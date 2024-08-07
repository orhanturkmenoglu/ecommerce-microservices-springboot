package com.example.spring.boot.external;

import com.example.spring.boot.dto.paymentDto.PaymentUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClientService {

    @PutMapping("/api/v1/payments")
    void updatePayment(@RequestBody PaymentUpdateRequestDto paymentUpdateRequestDto);
}

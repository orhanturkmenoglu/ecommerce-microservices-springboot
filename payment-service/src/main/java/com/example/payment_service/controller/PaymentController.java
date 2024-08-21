package com.example.payment_service.controller;

import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.processPayment(paymentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable("paymentId") String paymentId) {
        PaymentResponseDto paymentById = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentById);
    }

    @PutMapping
    public ResponseEntity<PaymentResponseDto> updatePayment(@RequestBody PaymentUpdateRequestDto paymentUpdateRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.updatePayment(paymentUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deleteByPaymentId(@PathVariable("paymentId") String paymentId) {
        try {
            paymentService.deleteByPaymentId(paymentId);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}

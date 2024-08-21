package com.example.payment_service.controller;

import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.processPayment(paymentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }

    @PostMapping("cancel/{paymentId}/")
    public ResponseEntity<String> cancelPayment(@PathVariable String paymentId) {
        try {
            paymentService.cancelPaymentById(paymentId);
            return ResponseEntity.ok("Payment cancelled successfully.");
        } catch (PaymentNotFoundException e) {
            log.error("PaymentController::cancelPayment - Payment cancellation failed. Payment ID: {} Error: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable("paymentId") String paymentId) {
        PaymentResponseDto paymentById = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentById);
    }

    // http://localhost:8085/api/v1/payments?paymentType=CREDIT_CARD
    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getPaymentType(@RequestParam("paymentType") String paymentType) {
        List<PaymentResponseDto> paymentsList = paymentService.getPaymentType(paymentType);
        return ResponseEntity.ok(paymentsList);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getPaymentDateBetween(@RequestParam("startDate") String startDate,
                                                                          @RequestParam("endDate") String endDate) {
        List<PaymentResponseDto> paymentsList = paymentService.getPaymentDateBetween(startDate, endDate);
        return ResponseEntity.ok(paymentsList);
    }

    @GetMapping("paymentCustomer/{customerId}")
    public ResponseEntity<PaymentResponseDto> getPaymentCustomerById(@PathVariable("customerId") String customerId) {
        PaymentResponseDto paymentById = paymentService.getPaymentCustomerById(customerId);
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

package com.example.payment_service.controller;

import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Process a payment", description = "Processes a payment and returns the payment response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<PaymentResponseDto> processPayment(
            @Parameter(description = "Payment request DTO containing payment details", required = true)
            @Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.processPayment(paymentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }

    @PostMapping("cancel/{paymentId}/")
    @Operation(summary = "Cancel a payment", description = "Cancels a payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Payment not found or cancellation failed")
    })
    public ResponseEntity<String> cancelPayment(
            @Parameter(description = "Unique identifier of the payment to be cancelled", required = true)
            @PathVariable String paymentId) {
        try {
            paymentService.cancelPaymentById(paymentId);
            return ResponseEntity.ok("Payment cancelled successfully.");
        } catch (PaymentNotFoundException e) {
            log.error("PaymentController::cancelPayment - Payment cancellation failed. Payment ID: {} Error: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @Parameter(description = "Unique identifier of the payment to retrieve", required = true)
            @PathVariable("paymentId") String paymentId) {
        PaymentResponseDto paymentById = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentById);
    }

    // http://localhost:8085/api/v1/payments?paymentType=CREDIT_CARD
    @GetMapping
    @Operation(summary = "Get payments by type", description = "Retrieves a list of payments filtered by payment type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    })
    public ResponseEntity<List<PaymentResponseDto>> getPaymentType(@Parameter(description = "Type of payment to filter by", required = true, example = "CREDIT_CARD")
                                                                   @RequestParam("paymentType") String paymentType) {
        List<PaymentResponseDto> paymentsList = paymentService.getPaymentType(paymentType);
        return ResponseEntity.ok(paymentsList);
    }

    @GetMapping("/paymentDateBetween")
    @Operation(summary = "Get payments between dates", description = "Retrieves a list of payments within a specified date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    })
    public ResponseEntity<List<PaymentResponseDto>> getPaymentDateBetween(
            @Parameter(description = "Start date of the range", example = "2024-08-01T00:00:00")
            @RequestParam("startDate") String startDate,
            @Parameter(description = "End date of the range", example = "2024-08-31T23:59:59")
            @RequestParam("endDate") String endDate) {
        List<PaymentResponseDto> paymentsList = paymentService.getPaymentDateBetween(startDate, endDate);
        return ResponseEntity.ok(paymentsList);
    }

    @GetMapping("paymentCustomer/{customerId}")
    @Operation(summary = "Get payments by customer ID", description = "Retrieves payment details by customer ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponseDto> getPaymentCustomerById(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable("customerId") String customerId) {
        PaymentResponseDto paymentById = paymentService.getPaymentCustomerById(customerId);
        return ResponseEntity.ok(paymentById);
    }

    @PutMapping
    @Operation(summary = "Update a payment", description = "Updates payment details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<PaymentResponseDto> updatePayment(
            @Parameter(description = "Payment update request DTO", required = true)
            @RequestBody PaymentUpdateRequestDto paymentUpdateRequestDto) {
        PaymentResponseDto paymentResponseDto = paymentService.updatePayment(paymentUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
    }


    @DeleteMapping("/{paymentId}")
    @Operation(summary = "Delete a payment", description = "Deletes a payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<Void> deleteByPaymentId(
            @Parameter(description = "Unique identifier of the payment to delete", required = true)
            @PathVariable("paymentId") String paymentId) {
        try {
            paymentService.deleteByPaymentId(paymentId);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}

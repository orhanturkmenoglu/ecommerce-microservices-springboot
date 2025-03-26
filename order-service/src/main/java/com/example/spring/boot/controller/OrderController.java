package com.example.spring.boot.controller;

import com.example.spring.boot.dto.orderDto.OrderRequestDto;
import com.example.spring.boot.dto.orderDto.OrderResponseDto;
import com.example.spring.boot.dto.orderDto.OrderUpdateRequestDto;
import com.example.spring.boot.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Tüm siparişleri listeleme
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orderResponseDtoList = orderService.getAllOrders();
        return ResponseEntity.ok(orderResponseDtoList);
    }

    // 2. Belirli bir siparişi ID'ye göre getirme
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable @Parameter(description = "ID of the order to retrieve") String id) {
        try {
            OrderResponseDto orderById = orderService.getOrderById(id);
            return ResponseEntity.ok(orderById);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }

    // http://localhost:8081/api/v1/orders?startDateTime=2024-08-21 10:00:00&endDateTime=2024-08-21 12:00:00
    @GetMapping("/orderDateBetween")
    @Operation(summary = "Get orders between dates", description = "Retrieve orders between specified start and end dates")
    public ResponseEntity<List<OrderResponseDto>> getByOrderDateBetween(
            @RequestParam @Parameter(description = "Start date and time for the query") String startDateTime,
            @RequestParam @Parameter(description = "End date and time for the query") String endDateTime) {
        List<OrderResponseDto> orderResponseDtoList = orderService.getByOrderDateBetween(startDateTime, endDateTime);
        return ResponseEntity.ok(orderResponseDtoList);
    }


    // 3. Yeni bir sipariş oluşturma
    @PostMapping
    @Operation(summary = "Create a new order", description = "Create a new order with the provided details")
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody @Parameter(description = "Details of the order to be created") OrderRequestDto orderRequestDto) throws Exception {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    // 4. Var olan siparişi güncelleme
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order", description = "Update the details of an existing order by its ID")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable @Parameter(description = "ID of the order to be updated") String id,
            @RequestBody @Parameter(description = "Updated details of the order") OrderUpdateRequestDto orderUpdateRequestDto) {
        try {
            OrderResponseDto updatedOrder = orderService.updateOrder(id, orderUpdateRequestDto);
            return ResponseEntity.ok(updatedOrder);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }

    // 5. Belirli bir siparişi silme
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Delete an order by its ID")
    public ResponseEntity<Void> deleteOrder(@PathVariable @Parameter(description = "ID of the order to be deleted") String id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }


}

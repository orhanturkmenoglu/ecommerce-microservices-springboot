package com.example.spring.boot.controller;

import com.example.spring.boot.dto.orderDto.OrderRequestDto;
import com.example.spring.boot.dto.orderDto.OrderResponseDto;
import com.example.spring.boot.dto.orderDto.OrderUpdateRequestDto;
import com.example.spring.boot.service.OrderService;
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
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orderResponseDtoList = orderService.getAllOrders();
        return ResponseEntity.ok(orderResponseDtoList);
    }

    // 2. Belirli bir siparişi ID'ye göre getirme
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable String id) {
        try {
            OrderResponseDto orderById = orderService.getOrderById(id);
            return ResponseEntity.ok(orderById);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }


    //http://localhost:8081/api/v1/orders?startDateTime=2024-08-21 10:00:00&endDateTime=2024-08-21 12:00:00
    @GetMapping("/orderDateBetween")
    public ResponseEntity<List<OrderResponseDto>> getByOrderDateBetween(@RequestParam("startDateTime") String startDateTime,
                                                                        @RequestParam("endDateTime") String endDateTime) {
        List<OrderResponseDto> orderResponseDtoList = orderService.getByOrderDateBetween(startDateTime,endDateTime);
        return ResponseEntity.ok(orderResponseDtoList);
    }

    // 3. Yeni bir sipariş oluşturma
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    // 4. Var olan siparişi güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable String id, @RequestBody OrderUpdateRequestDto orderUpdateRequestDto) {
        try {
            OrderResponseDto updatedOrder = orderService.updateOrder(id, orderUpdateRequestDto);
            return ResponseEntity.ok(updatedOrder);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }

    // 5. Belirli bir siparişi silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }


}

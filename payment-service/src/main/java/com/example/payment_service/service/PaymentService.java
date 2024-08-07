package com.example.payment_service.service;

import com.example.payment_service.dto.orderDto.OrderResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.exception.InsufficientStockException;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.external.InventoryServiceClient;
import com.example.payment_service.external.OrderServiceClient;
import com.example.payment_service.mapper.PaymentMapper;
import com.example.payment_service.model.Inventory;
import com.example.payment_service.model.Payment;
import com.example.payment_service.publisher.PaymentMessageSender;
import com.example.payment_service.repository.PaymentRepository;
import com.example.payment_service.util.PaymentMessage;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderServiceClient orderServiceClient;

    private final InventoryServiceClient inventoryServiceClient;

    private final PaymentMapper paymentMapper;

    private final PaymentMessageSender paymentMessageSender;

    @Transactional
    @CircuitBreaker(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @Retry(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @RateLimiter(name = "processPaymentLimiter", fallbackMethod = "paymentServiceFallback")
    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequestDto) {
        log.info("PaymentService::processPayment started");

        // sipariş kontrolü gerçekleştir..
        OrderResponseDto order = orderServiceClient.getOrderById(paymentRequestDto.getOrderId());
        Inventory inventory = inventoryServiceClient.getInventoryById(order.getInventoryId());
        log.info("PaymentResponseDto::processPayment - Order with id : {}", order.getId());
        log.info("PaymentResponseDto::processPayment - inventory with id : {}", inventory.getId());


        // ödeme oluştur..
        Payment payment = paymentMapper.mapToPayment(paymentRequestDto);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setAmount(order.getTotalAmount());
        log.info("PaymentResponseDto::processPayment - payment: {}", payment);


        // ödemeyi kaydet.
        Payment savedPayment = paymentRepository.save(payment);
        log.info("PaymentResponseDto::processPayment - saved payment: {}", savedPayment);


        // ödeme başarılı ise stok güncellenecek...
        updatedInventory(order, inventory);

        // ödeme alındığına dair bildirim gönder.

        return paymentMapper.mapToPaymentResponseDto(savedPayment);
    }

    @Transactional
    @CircuitBreaker(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @Retry(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @RateLimiter(name = "processPaymentLimiter", fallbackMethod = "paymentServiceFallback")
    public PaymentResponseDto updatePayment(PaymentUpdateRequestDto paymentUpdateRequestDto) {
        log.info("PaymentService::updatePayment started");

        Payment payment = paymentRepository.findByOrderId(paymentUpdateRequestDto.getOrderId())
                .orElseThrow(() ->
                        new PaymentNotFoundException(PaymentMessage.PAYMENT_NOT_FOUND + paymentUpdateRequestDto.getOrderId()));


        log.info("PaymentResponseDto::updatePayment - payment : {}", payment);

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentType(paymentUpdateRequestDto.getPaymentType());
        payment.setAmount(paymentUpdateRequestDto.getAmount());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("PaymentResponseDto::updatePayment - saved payment : {}", savedPayment);

        return paymentMapper.mapToPaymentResponseDto(savedPayment);
    }

    private void updatedInventory(OrderResponseDto orderResponseDto, Inventory inventory) {
        // stok kontrolü
        if (inventory.getStockQuantity() < orderResponseDto.getQuantity()) {
            log.error("Not enough stock for product id: {}", inventory.getId());
            throw new InsufficientStockException(PaymentMessage.INSUFFICIENT_STOCK);
        }

        inventory.setStockQuantity(inventory.getStockQuantity() - orderResponseDto.getQuantity());

        //Envanter güncelleme işlemini RabbitMQ kullanarak mesaj kuyruğuna gönderiyoruz
        paymentMessageSender.sendInventoryUpdateMessage(inventory);
    }

    private PaymentResponseDto paymentServiceFallback(Exception exception) {
        log.info("fallback is executed because servise is down :{}", exception.getMessage());
        return PaymentResponseDto.builder()
                .orderId("")
                .amount(0.0)
                .paymentStatus(PaymentStatus.FAILED)
                .build();
    }

}

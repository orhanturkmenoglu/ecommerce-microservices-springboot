package com.example.payment_service.service;

import com.example.payment_service.dto.orderDto.OrderResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.exception.InsufficientStockException;
import com.example.payment_service.exception.PaymentCustomerNotFoundException;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.external.CustomerClientService;
import com.example.payment_service.external.InventoryServiceClient;
import com.example.payment_service.external.OrderServiceClient;
import com.example.payment_service.mapper.PaymentMapper;
import com.example.payment_service.model.Customer;
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

    private final CustomerClientService customerClientService;

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
        Customer customer = customerClientService.getCustomerById(paymentRequestDto.getCustomerId());
        // müşteriyi kontrol et

        log.info("PaymentResponseDto::processPayment - customer with id : {},  Order with id : {}," +
                " inventory with id : {}", customer.getId(), order.getId(), inventory.getId());


        // ödeme oluştur..
        Payment payment = paymentMapper.mapToPayment(paymentRequestDto);
        payment.setAmount(order.getTotalAmount());
        log.info("PaymentResponseDto::processPayment - payment: {}", payment);


        // ödemeyi kaydet.
        Payment savedPayment = paymentRepository.save(payment);
        log.info("PaymentResponseDto::processPayment - saved payment: {}", savedPayment);


        // ödeme başarılı ise stok rabbitmq yardımı ile güncellenecek...
        updatedInventory(order, inventory);

        // ödeme alındığına dair bildirim gönder.
        PaymentResponseDto paymentResponseDto = paymentMapper.mapToPaymentResponseDto(savedPayment);
        paymentResponseDto.setCustomer(customer);
        log.info("PaymentResponseDto::processPayment - paymentResponseDto : {}", paymentResponseDto);

        return paymentResponseDto;
    }


    public PaymentResponseDto getPaymentById(String paymentId) {
        log.info("PaymentService::getPaymentById started");

        Payment payment = getPayment(paymentId);
        log.info("PaymentResponseDto::getPaymentById -payment : {}", payment);

        log.info("PaymentService::getPaymentById finished");
        return paymentMapper.mapToPaymentResponseDto(payment);
    }


    @Transactional
    @CircuitBreaker(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @Retry(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @RateLimiter(name = "processPaymentLimiter", fallbackMethod = "paymentServiceFallback")
    public PaymentResponseDto updatePayment(PaymentUpdateRequestDto paymentUpdateRequestDto) {
        log.info("PaymentService::updatePayment started");

        OrderResponseDto order = orderServiceClient.getOrderById(paymentUpdateRequestDto.getOrderId());
        Customer customer = customerClientService.getCustomerById(paymentUpdateRequestDto.getCustomerId());

        Payment paymentCustomerById = paymentRepository.findByCustomerId(paymentUpdateRequestDto.getCustomerId())
                .orElseThrow(() ->
                        new PaymentCustomerNotFoundException(PaymentMessage.PAYMENT_CUSTOMER_NOT_FOUND +
                                paymentUpdateRequestDto.getCustomerId()));

        log.info("PaymentResponseDto::updatePayment - order : {}", order);
        log.info("PaymentResponseDto::updatePayment - customer : {}", customer);
        log.info("PaymentResponseDto::updatePayment - paymentCustomerById : {}", paymentCustomerById);

        paymentCustomerById.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentCustomerById.setPaymentType(paymentUpdateRequestDto.getPaymentType());
        paymentCustomerById.setAmount(order.getTotalAmount());  // BURASI ORDER-SERVICEDEN GELECEK
        Payment savedPayment = paymentRepository.save(paymentCustomerById);
        log.info("PaymentResponseDto::updatePayment - saved payment : {}", savedPayment);

        PaymentResponseDto paymentResponseDto = paymentMapper.mapToPaymentResponseDto(savedPayment);
        paymentResponseDto.setCustomer(customer);
        log.info("PaymentResponseDto::updatePayment -paymentResponseDto : {}", paymentResponseDto);

        log.info("PaymentService::updatePayment finished");
        return paymentResponseDto;
    }


    public void deleteByPaymentId(String paymentId) {
        log.info("PaymentService::deleteByPaymentId started");

        Payment payment = getPayment(paymentId);
        log.info("PaymentResponseDto::deleteByPaymentId -payment : {}", payment);


        paymentRepository.deleteById(payment.getId());
        log.info("PaymentService::deleteByPaymentId finished");
    }


    private void updatedInventory(OrderResponseDto orderResponseDto, Inventory inventory) {
        // stok kontrolü
        if (inventory.getStockQuantity() < orderResponseDto.getQuantity()) {
            log.error("Not enough stock for product id: {}", inventory.getId());
            throw new InsufficientStockException(PaymentMessage.INSUFFICIENT_STOCK);
        }
        // Stok miktarını güncelle
        inventory.setStockQuantity(inventory.getStockQuantity() - orderResponseDto.getQuantity());

        try {
            //Envanter güncelleme işlemini RabbitMQ kullanarak mesaj kuyruğuna gönderiyoruz
            paymentMessageSender.sendInventoryUpdateMessage(inventory);
            log.info("Inventory update message sent for product id: {}", inventory.getId());
        } catch (Exception e) {
            // RabbitMQ ile mesaj gönderme sırasında bir hata oluştu
            log.error("Failed to send inventory update message for product id: {}. Error: {}", inventory.getId(), e.getMessage(), e);
        }
    }

    private Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() ->
                new PaymentNotFoundException(PaymentMessage.PAYMENT_NOT_FOUND + paymentId));
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

package com.example.payment_service.service;

import com.example.payment_service.dto.orderDto.OrderResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.enums.CargoStatus;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.enums.PaymentType;
import com.example.payment_service.exception.InsufficientStockException;
import com.example.payment_service.exception.PaymentCustomerNotFoundException;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.external.CargoClientService;
import com.example.payment_service.external.CustomerClientService;
import com.example.payment_service.external.InventoryServiceClient;
import com.example.payment_service.external.OrderServiceClient;
import com.example.payment_service.mapper.PaymentMapper;
import com.example.payment_service.model.Cargo;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderServiceClient orderServiceClient;

    private final InventoryServiceClient inventoryServiceClient;

    private final CustomerClientService customerClientService;

    private final CargoClientService cargoClientService;

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

        log.info("PaymentResponseDto::processPayment - customer with id : {},  Order with id : {}," +
                " inventory with id : {}", customer.getId(), order.getId(), inventory.getId());


        // ödeme oluştur..
        Payment payment = paymentMapper.mapToPayment(paymentRequestDto);
        payment.setQuantity(order.getQuantity());
        payment.setAmount(order.getTotalAmount());
        log.info("PaymentResponseDto::processPayment - payment: {}", payment);


        // ödemeyi kaydet.
        Payment savedPayment = paymentRepository.save(payment);
        log.info("PaymentResponseDto::processPayment - saved payment: {}", savedPayment);


        // ödeme başarılı ise kontrol et. stok rabbitmq yardımı ile güncellenecek...
        updatedInventory(order, inventory);

        // ödeme başarılı ise cargo statüsünü güncelle.
        if (savedPayment.getPaymentStatus().name().equals(PaymentStatus.COMPLETED.name())) {
           updateCargoStatus(savedPayment);
        }


        PaymentResponseDto paymentResponseDto = paymentMapper.mapToPaymentResponseDto(savedPayment);
        paymentResponseDto.setCustomer(customer);
        log.info("PaymentResponseDto::processPayment - paymentResponseDto : {}", paymentResponseDto);

        return paymentResponseDto;
    }

    @Cacheable(value = "payments", key = "#paymentId")
    public PaymentResponseDto getPaymentById(String paymentId) {
        log.info("PaymentService::getPaymentById started");

        Payment payment = getPayment(paymentId);
        log.info("PaymentResponseDto::getPaymentById -payment : {}", payment);

        log.info("PaymentService::getPaymentById finished");
        return paymentMapper.mapToPaymentResponseDto(payment);
    }

    @Cacheable(value = "payments", key = "#paymentType")
    public List<PaymentResponseDto> getPaymentType(String paymentType) {
        log.info("PaymentService::getPaymentType started");

        // Enum dönüşümünü ve geçersiz değerleri kontrol etme
        PaymentType paymentTypeEnum;
        try {
            paymentTypeEnum = PaymentType.valueOf(paymentType.toUpperCase());
            log.info("PaymentResponseDto::getPaymentType - paymentTypeEnum : {}", paymentTypeEnum);
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment type provided: {}", paymentType);
            // Hata durumunda uygun bir işlem yapabilirsiniz, örneğin özel bir istisna fırlatma
            throw new IllegalArgumentException("Invalid payment type: " + paymentType);
        }

        // Veritabanından belirtilen ödeme türüne göre ödeme bilgilerini al
        List<Payment> paymentList = paymentRepository.findByPaymentType(paymentTypeEnum);
        log.info("PaymentResponseDto::getPaymentType - paymentList : {}", paymentList);

        // Filtreleme ve dönüştürme
        List<Payment> payments = paymentList.stream()
                .filter(payment -> payment.getPaymentType() == paymentTypeEnum)
                .toList();


        log.info("PaymentResponseDto::getPaymentType - payments : {}", payments);
        log.info("PaymentService::getPaymentType finished");
        return paymentMapper.maptoPaymentResponseDtoList(payments);
    }

    @Cacheable(value = "payments", key = "'dateRange:' + #startDate + '-' + #endDate")
    public List<PaymentResponseDto> getPaymentDateBetween(String startDate, String endDate) {
        log.info("PaymentService::getPaymentDateBetween started");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        log.info("PaymentResponseDto::getPaymentDateBetween -formatter : {}" +
                "start : {} ,end : {}", formatter, start, end);

        List<Payment> paymentList = paymentRepository.findByPaymentDateBetween(start, end);

        log.info("PaymentResponseDto::getPaymentDateBetween -paymentList : {}", paymentList);

        log.info("PaymentService::getPaymentDateBetween finished");
        return paymentMapper.maptoPaymentResponseDtoList(paymentList);
    }

    @Cacheable(value = "payments", key = "#customerId")
    public PaymentResponseDto getPaymentCustomerById(String customerId) {
        log.info("PaymentService::getPaymentCustomerById started");

        Payment paymentCustomerById = getPaymentCustomer(customerId);
        log.info("PaymentResponseDto::getPaymentById -paymentCustomerById : {}", paymentCustomerById);

        log.info("PaymentService::getPaymentCustomerById finished");
        return paymentMapper.mapToPaymentResponseDto(paymentCustomerById);
    }


    @Transactional
    @CircuitBreaker(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @Retry(name = "paymentServiceBreaker", fallbackMethod = "paymentServiceFallback")
    @RateLimiter(name = "processPaymentLimiter", fallbackMethod = "paymentServiceFallback")
    @CacheEvict(value = "payments", key = "#paymentUpdateRequestDto.customerId",allEntries = true)
    public PaymentResponseDto updatePayment(PaymentUpdateRequestDto paymentUpdateRequestDto) {
        log.info("PaymentService::updatePayment started");

        OrderResponseDto order = orderServiceClient.getOrderById(paymentUpdateRequestDto.getOrderId());
        Inventory inventory = inventoryServiceClient.getInventoryById(order.getInventoryId());
        Customer customer = customerClientService.getCustomerById(paymentUpdateRequestDto.getCustomerId());

        Payment paymentCustomerById = getPaymentCustomer(paymentUpdateRequestDto.getCustomerId());

        log.info("PaymentResponseDto::updatePayment - order : {}", order);
        log.info("PaymentResponseDto::updatePayment - customer : {}", customer);
        log.info("PaymentResponseDto::updatePayment - paymentCustomerById : {}", paymentCustomerById);

        paymentCustomerById.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentCustomerById.setPaymentType(paymentUpdateRequestDto.getPaymentType());
        paymentCustomerById.setQuantity(order.getQuantity());
        paymentCustomerById.setAmount(order.getTotalAmount());

        Payment savedPayment = paymentRepository.save(paymentCustomerById);
        log.info("PaymentResponseDto::updatePayment - saved payment : {}", savedPayment);

        PaymentResponseDto paymentResponseDto = paymentMapper.mapToPaymentResponseDto(savedPayment);
        paymentResponseDto.setCustomer(customer);
        log.info("PaymentResponseDto::updatePayment -paymentResponseDto : {}", paymentResponseDto);

        // ödeme başarılı ise stok rabbitmq yardımı ile güncellenecek...
        updatedInventory(order, inventory);

        log.info("PaymentService::updatePayment finished");
        return paymentResponseDto;
    }

    @Transactional
    public void cancelPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for ID: " + paymentId));

        // Ödeme durumu iptal olarak güncellenir
        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        // İptal edilen ödeme kaydedilir
        paymentRepository.save(payment);
        log.info("PaymentService::cancelPaymentById - Payment cancelled successfully. Payment ID: {}", paymentId);
    }

    @CacheEvict(value = "payments", key = "#paymentId",allEntries = true)
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

    private Payment getPaymentCustomer(String customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new PaymentCustomerNotFoundException(PaymentMessage.PAYMENT_CUSTOMER_NOT_FOUND + customerId));
    }

    private void updateCargoStatus(Payment savedPayment) {
        Cargo cargo = Cargo.builder()
                .id(savedPayment.getCargoId())
                .customerId(savedPayment.getCustomerId())
                .orderId(savedPayment.getOrderId())
                .status(CargoStatus.IN_TRANSIT)
                .build();

        try {
            cargoClientService.updateCargo(cargo);
            log.info("Cargo status updated for order id: {}", savedPayment.getOrderId());
        } catch (Exception e) {
            log.error("Failed to update cargo status for order id: {}", savedPayment.getOrderId(), e);
        }
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

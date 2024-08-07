package com.example.spring.boot.service;

import com.example.spring.boot.dto.inventoryDto.InventoryUpdateRequestDto;
import com.example.spring.boot.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.spring.boot.dto.orderDto.OrderRequestDto;
import com.example.spring.boot.dto.orderDto.OrderResponseDto;
import com.example.spring.boot.exception.InsufficientStockException;
import com.example.spring.boot.exception.OrderNotFoundException;
import com.example.spring.boot.external.InventoryClientService;
import com.example.spring.boot.external.PaymentClientService;
import com.example.spring.boot.external.ProductClientService;
import com.example.spring.boot.mapper.OrderMapper;
import com.example.spring.boot.model.Inventory;
import com.example.spring.boot.model.Order;
import com.example.spring.boot.model.Product;
import com.example.spring.boot.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClientService inventoryClientService;

    private final ProductClientService productClientService;

    private final PaymentClientService paymentClientService;

    private final OrderMapper orderMapper;

    public List<OrderResponseDto> getAllOrders() {
        log.info("OrderService::getAllOrders started");

        List<Order> orderList = orderRepository.findAll();
        log.info("OrderResponseDto::getAllOrders - orderList: {}", orderList);

        log.info("OrderService::getAllOrders finish");
        return orderMapper.mapToOrderResponseDtoList(orderList);
    }

    public OrderResponseDto getOrderById(String id) {
        log.info("OrderService::getOrderById started");

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for ID: " + id));

        log.info("OrderService::getOrderById finish");
        return orderMapper.mapToOrderResponseDto(order);
    }


    @Transactional
    @Retry(name = "orderService", fallbackMethod = "fallbackCreateOrder")
    @CircuitBreaker(name = "orderServiceBreaker", fallbackMethod = "fallbackCreateOrder")
    @RateLimiter(name = "createOrderLimiter", fallbackMethod = "fallbackCreateOrder")
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Order::createOrder started");

        // Feign Client ile senkron olarak Product ve Inventory servislerine çağrı yapıyoruz
        Product product = productClientService.getProductById(orderRequestDto.getProductId());
        Inventory inventory = inventoryClientService.getInventoryById(orderRequestDto.getInventoryId());

        log.info("OrderResponseDto::createOrder - Order create with product id : {}", product.getId());
        log.info("OrderResponseDto::createOrder - Order create with inventory id: {}", inventory.getId());


        // stok kontrolü yap.
        // sipariş oluştur
        if (!(inventory.getStockQuantity() >= orderRequestDto.getQuantity())) {
            log.info("OrderResponseDto::createOrder - Order create with orderRequestDto.getQuantity : {}", orderRequestDto.getQuantity());
            throw new InsufficientStockException("Yeterli stok mevcut değil !");
        }

        OrderResponseDto savedOrder = getSaveOrder(orderRequestDto, product);
        log.info("OrderResponseDto::createOrder - saved order: {}", savedOrder);


        log.info("Order::createOrder finished.");
        return savedOrder;
    }


    @Transactional
    @Retry(name = "orderService", fallbackMethod = "fallbackCreateOrder")
    @CircuitBreaker(name = "orderServiceBreaker", fallbackMethod = "fallbackCreateOrder")
    @RateLimiter(name = "createOrderLimiter", fallbackMethod = "fallbackCreateOrder")
    public OrderResponseDto updateOrder(String id, OrderRequestDto orderRequestDto) {
        log.info("OrderService::updateOrder started");

        OrderResponseDto existingOrder = getOrderById(id);

        // Feign Client ile senkron olarak Product ve Inventory servislerine çağrı yapıyoruz
        Inventory inventory = inventoryClientService.getInventoryById(orderRequestDto.getInventoryId());
        Product product = productClientService.getProductById(orderRequestDto.getProductId());


        log.info("OrderResponseDto::updateOrder - existingOrder with order id : {}", existingOrder.getId());
        log.info("OrderResponseDto::updateOrder - inventory with id : {}", inventory.getId());

        int currentOrderQuantity = existingOrder.getQuantity();
        int newOrderQuantity = orderRequestDto.getQuantity();
        int updatedInventoryQuantity = inventory.getStockQuantity() + currentOrderQuantity - newOrderQuantity;


        existingOrder.setQuantity(newOrderQuantity);
        existingOrder.setShippingAddress(orderRequestDto.getShippingAddress());
        existingOrder.setTotalAmount((product.getPrice() * orderRequestDto.getQuantity()));

        Order mapToOrder = orderMapper.mapToOrder(existingOrder);
        log.info("OrderResponseDto::updateOrder - mapToOrder  : {}", mapToOrder);

        // sipariş güncelle.
        Order save = orderRepository.save(mapToOrder);
        log.info("OrderResponseDto::updateOrder - save order : {}", save);


        // Envanter güncelleme isteğini Feign Client ile gönderiyoruz
        InventoryUpdateRequestDto updateRequest = getInventoryUpdateRequestDto(orderRequestDto, save, updatedInventoryQuantity);
        log.info("OrderResponseDto::updateOrder - updateRequest  : {}", updateRequest);

        // stok güncelle
        inventoryClientService.updateInventory(inventory.getId(), updateRequest);
        log.info("OrderService::updateOrder finish");


        // Ödeme güncelleme isteğini Feign Client ile gönderiyoruz
        // ödeme güncelle
        PaymentUpdateRequestDto updatePayment = getUpdatePayment(product, save);
        log.info("OrderResponseDto::updateOrder - updatePayment  : {}", updatePayment);

        paymentClientService.updatePayment(updatePayment);

        return orderMapper.mapToOrderResponseDto(save);
    }


    public void deleteOrder(String id) {
        log.info("OrderService::deleteOrder started");

        OrderResponseDto orderResponseDto = getOrderById(id);
        Order order = orderMapper.mapToOrder(orderResponseDto);

        orderRepository.delete(order);

        log.info("OrderService::deleteOrder finish");
    }

    private static PaymentUpdateRequestDto getUpdatePayment(Product product, Order order) {
        return PaymentUpdateRequestDto.builder()
                .orderId(order.getId())
                .amount(product.getPrice() * order.getQuantity())
                .build();
    }


    private OrderResponseDto getSaveOrder(OrderRequestDto orderRequestDto, Product product) {
        orderRequestDto.setProductId(product.getId());
        orderRequestDto.setInventoryId(product.getInventoryId());
        orderRequestDto.setOrderDate(LocalDateTime.now());

        Order saveOrder = orderMapper.mapToOrder(orderRequestDto);
        saveOrder.setTotalAmount((product.getPrice() * orderRequestDto.getQuantity()));
        orderRepository.save(saveOrder);

        log.info("OrderResponseDto::getSaveOrder - saveOrder  : {}", saveOrder);
        return orderMapper.mapToOrderResponseDto(saveOrder);
    }

    private OrderResponseDto fallbackCreateOrder(OrderRequestDto orderRequestDto, Throwable t) {
        log.error("Error creating order: ", t);
        return new OrderResponseDto(); // or any fallback response
    }

    private static InventoryUpdateRequestDto getInventoryUpdateRequestDto(OrderRequestDto orderRequestDto, Order save, int updatedInventoryQuantity) {
        return InventoryUpdateRequestDto.builder()
                .inventoryId(save.getInventoryId())
                .newQuantity(updatedInventoryQuantity)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

}

package com.example.spring.boot.service;

import com.example.spring.boot.dto.cargoDto.CargoRequestDto;
import com.example.spring.boot.dto.cargoDto.CargoUpdateRequestDto;
import com.example.spring.boot.dto.inventoryDto.InventoryUpdateRequestDto;
import com.example.spring.boot.dto.orderDto.OrderRequestDto;
import com.example.spring.boot.dto.orderDto.OrderResponseDto;
import com.example.spring.boot.dto.orderDto.OrderUpdateRequestDto;
import com.example.spring.boot.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.spring.boot.enums.OrderStatus;
import com.example.spring.boot.exception.InsufficientStockException;
import com.example.spring.boot.exception.OrderNotFoundException;
import com.example.spring.boot.external.*;
import com.example.spring.boot.mapper.OrderMapper;
import com.example.spring.boot.model.*;
import com.example.spring.boot.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClientService inventoryClientService;

    private final ProductClientService productClientService;

    private final PaymentClientService paymentClientService;

    private final CustomerClientService customerClientService;

    private final CargoClientService cargoClientService;

    private final OrderMapper orderMapper;

    @Cacheable(value = "orders", key = "'all'")
    public List<OrderResponseDto> getAllOrders() {
        log.info("OrderService::getAllOrders started");

        List<Order> orderList = orderRepository.findAll();
        log.info("OrderResponseDto::getAllOrders - orderList: {}", orderList);

        log.info("OrderService::getAllOrders finish");
        return orderMapper.mapToOrderResponseDtoList(orderList);
    }

    @Cacheable(value = "orders", key = "#id")
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
        Customer customer = customerClientService.getCustomerById(orderRequestDto.getCustomerId());


        log.info("OrderResponseDto::createOrder - Order create with product id: {}, inventory id: {}, customer id: {}",
                product.getId(), inventory.getId(), customer.getId());


        // stok kontrolü yap.
        // sipariş oluştur
        if (!(inventory.getStockQuantity() >= orderRequestDto.getQuantity())) {
            log.info("OrderResponseDto::createOrder - Order create with orderRequestDto.getQuantity : {}", orderRequestDto.getQuantity());
            throw new InsufficientStockException("Yeterli stok mevcut değil !");
        }

        List<Address> addressList = customer.getAddressList();
        Address address = addressList.stream().findFirst().orElseThrow();
        log.info("OrderResponseDto::createOrder - addressList: {}", addressList);
        log.info("OrderResponseDto::createOrder - address: {}", address);


        OrderResponseDto savedOrder = getSaveOrder(orderRequestDto, product, address);
        log.info("OrderResponseDto::createOrder - saved order: {}", savedOrder);

        //sipariş oluşturulduğunda cargo-service ile iletişim sağlanıp kargo kaydı oluşturulacak.

        CargoRequestDto cargo = getCargo(savedOrder.getId(), customer.getId());

        cargoClientService.createCargo(cargo);

        log.info("Order::createOrder finished.");
        return savedOrder;
    }

    @Cacheable(value = "orders", key = "#startDateTime + '-' + #endDateTime")
    public List<OrderResponseDto> getByOrderDateBetween(String startDateTime, String endDateTime) {
        log.info("Order::getByOrderDateBetween started.");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDateTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endDateTime, formatter);

        log.info("List<OrderResponseDto>::getByOrderDateBetween - formatter : {} ," +
                " start : {} , end : {}", formatter, start, end);

        List<Order> orderList = orderRepository.findByOrderDateBetween(start, end);
        log.info("OrderResponseDto::getByOrderDateBetween - orderList : {}", orderList);

        log.info("Order::getByOrderDateBetween finished.");
        return orderMapper.mapToOrderResponseDtoList(orderList);
    }


    @Transactional
    @Retry(name = "orderService", fallbackMethod = "fallbackUpdateOrder")
    @CircuitBreaker(name = "orderServiceBreaker", fallbackMethod = "fallbackUpdateOrder")
    @RateLimiter(name = "createOrderLimiter", fallbackMethod = "fallbackUpdateOrder")
    @CacheEvict(value = "orders", key = "#id",allEntries = true)
    public OrderResponseDto updateOrder(String id, OrderUpdateRequestDto orderUpdateRequestDto) {
        log.info("OrderService::updateOrder - Order update process started. Order ID: {}", id);

        OrderResponseDto existingOrder = getOrderById(id);

        // Feign Client ile senkron olarak Product ve Inventory servislerine çağrı yapıyoruz
        Inventory inventory = inventoryClientService.getInventoryById(orderUpdateRequestDto.getInventoryId());
        Product product = productClientService.getProductById(orderUpdateRequestDto.getProductId());
        Customer customer = customerClientService.getCustomerById(orderUpdateRequestDto.getCustomerId());

        log.info("OrderResponseDto::updateOrder - " +
                        "existingOrder with order id : {} ," +
                        " inventory with id : {}, customer with id : {} ",
                existingOrder.getId(), inventory.getId(), customer.getId());


        int currentOrderQuantity = existingOrder.getQuantity();
        int newOrderQuantity = orderUpdateRequestDto.getQuantity();
        int updatedInventoryQuantity = inventory.getStockQuantity() + currentOrderQuantity;


        if (orderUpdateRequestDto.getShippingAddress() == null) {
            List<Address> addressList = customer.getAddressList();
            Address address = addressList.stream().findFirst().orElseThrow();

            log.info("OrderService::updateOrder -" +
                            " Shipping address added to order. Customer ID: {}, Address: {}",
                    customer.getId(), existingOrder.getShippingAddress());

            existingOrder.setShippingAddress(address);
        }

        existingOrder.setQuantity(newOrderQuantity);
        existingOrder.setTotalAmount((product.getPrice() * orderUpdateRequestDto.getQuantity()));


        Order mapToOrder = orderMapper.mapToOrder(existingOrder);
        log.info("OrderResponseDto::updateOrder - mapToOrder  : {}", mapToOrder);

        // sipariş güncelle.
        Order updatedOrder = orderRepository.save(mapToOrder);
        log.info("OrderService::updateOrder - Updating order. Order ID: {}, Quantity: {}, Total Amount: {}",
                updatedOrder.getId(), updatedOrder.getQuantity(), updatedOrder.getTotalAmount());


        // ÖDEMEYİ İPTAL ET ...
        paymentClientService.cancelPayment(orderUpdateRequestDto.getPaymentId());

        // ödeme güncelleme işlemine yönlendir.
        // ödeme güncellendikten sonra stok miktari eski haline getir.

        InventoryUpdateRequestDto updateRequest = getInventoryUpdateRequestDto(updatedOrder, updatedInventoryQuantity);
        log.info("OrderResponseDto::updateOrder - updateRequest  : {}", updateRequest);

        // Envanter güncelleme isteğini Feign Client ile gönderiyoruz
        inventoryClientService.updateInventory(inventory.getId(), updateRequest);


        log.info("OrderService::updateOrder - Order update process completed successfully. Updated Order ID: {}", updatedOrder.getId());
        return orderMapper.mapToOrderResponseDto(updatedOrder);
    }

    @CacheEvict(value = "orders", key = "#id",allEntries = true)
    public void deleteOrder(String id) {
        log.info("OrderService::deleteOrder started");

        OrderResponseDto orderResponseDto = getOrderById(id);
        Order order = orderMapper.mapToOrder(orderResponseDto);

        log.info("OrderResponseDto::deleteOrder - orderResponseDto  : {}" +
                "order : {}", orderResponseDto, order);

        orderRepository.delete(order);

        log.info("OrderService::deleteOrder finish");
    }

    private static PaymentUpdateRequestDto getPaymentUpdate(Product product, Customer customer, Order updatedOrder, OrderResponseDto existingOrder) {
        double updatedPrice = product.getPrice() * updatedOrder.getQuantity();
        return PaymentUpdateRequestDto.builder()
                .customerId(customer.getId())
                .orderId(existingOrder.getId())
                .amount(updatedPrice)
                .build();
    }

    private OrderResponseDto getSaveOrder(OrderRequestDto orderRequestDto, Product product, Address address) {
        orderRequestDto.setShippingAddress(address);
        Order saveOrder = orderMapper.mapToOrder(orderRequestDto);
        saveOrder.setTotalAmount((product.getPrice() * orderRequestDto.getQuantity()));
        saveOrder.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(saveOrder);

        log.info("OrderResponseDto::getSaveOrder - saveOrder  : {}", saveOrder);
        return orderMapper.mapToOrderResponseDto(saveOrder);
    }

    private static InventoryUpdateRequestDto getInventoryUpdateRequestDto(Order save, int updatedInventoryQuantity) {
        return InventoryUpdateRequestDto.builder()
                .inventoryId(save.getInventoryId())
                .newQuantity(updatedInventoryQuantity)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    private static CargoRequestDto getCargo(String orderId,String customerId) {
        return CargoRequestDto.builder()
                .orderId(orderId)
                .customerId(customerId)
                .build();
    }

    private OrderResponseDto fallbackCreateOrder(OrderRequestDto orderRequestDto, Throwable t) {
        log.error("Error creating order: ", t);
        return new OrderResponseDto(); // or any fallback response
    }

    private OrderResponseDto fallbackUpdateOrder(OrderRequestDto orderRequestDto, Throwable t) {
        log.error("Error update  order: ", t);
        return new OrderResponseDto(); // or any fallback response
    }


}

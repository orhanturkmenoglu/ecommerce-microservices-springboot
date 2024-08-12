package com.example.spring.boot.service;

import com.example.spring.boot.dto.inventoryDto.InventoryUpdateRequestDto;
import com.example.spring.boot.dto.orderDto.OrderRequestDto;
import com.example.spring.boot.dto.orderDto.OrderResponseDto;
import com.example.spring.boot.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.spring.boot.enums.OrderStatus;
import com.example.spring.boot.exception.InsufficientStockException;
import com.example.spring.boot.exception.OrderNotFoundException;
import com.example.spring.boot.external.CustomerClientService;
import com.example.spring.boot.external.InventoryClientService;
import com.example.spring.boot.external.PaymentClientService;
import com.example.spring.boot.external.ProductClientService;
import com.example.spring.boot.mapper.OrderMapper;
import com.example.spring.boot.model.*;
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

    private final CustomerClientService customerClientService;

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

        log.info("Order::createOrder finished.");
        return savedOrder;
    }


    @Transactional
    @Retry(name = "orderService", fallbackMethod = "fallbackUpdateOrder")
    @CircuitBreaker(name = "orderServiceBreaker", fallbackMethod = "fallbackUpdateOrder")
    @RateLimiter(name = "createOrderLimiter", fallbackMethod = "fallbackUpdateOrder")
    public OrderResponseDto updateOrder(String id, OrderRequestDto orderRequestDto) {
        log.info("OrderService::updateOrder started");

        OrderResponseDto existingOrder = getOrderById(id);

        // Feign Client ile senkron olarak Product ve Inventory servislerine çağrı yapıyoruz
        Inventory inventory = inventoryClientService.getInventoryById(orderRequestDto.getInventoryId());
        Product product = productClientService.getProductById(orderRequestDto.getProductId());
        Customer customer = customerClientService.getCustomerById(orderRequestDto.getCustomerId());

        log.info("OrderResponseDto::updateOrder - " +
                        "existingOrder with order id : {} ," +
                        " inventory with id : {}, inventory with id : {} ",
                existingOrder.getId(), inventory.getId(), customer.getId());


        int currentOrderQuantity = existingOrder.getQuantity();
        int newOrderQuantity = orderRequestDto.getQuantity();
        int updatedInventoryQuantity = inventory.getStockQuantity() + currentOrderQuantity - newOrderQuantity;


        if (orderRequestDto.getShippingAddress() == null) {
            List<Address> addressList = customer.getAddressList();
            Address address = addressList.stream().findFirst().orElseThrow();

            log.info("OrderResponseDto::updateOrder - addressList: {} ," +
                    "  address: {}", addressList, address);

            existingOrder.setShippingAddress(address);
        }

        existingOrder.setQuantity(newOrderQuantity);
        existingOrder.setTotalAmount((product.getPrice() * orderRequestDto.getQuantity()));


        Order mapToOrder = orderMapper.mapToOrder(existingOrder);
        log.info("OrderResponseDto::updateOrder - mapToOrder  : {}", mapToOrder);

        // sipariş güncelle.
        Order updatedOrder = orderRepository.save(mapToOrder);
        log.info("OrderResponseDto::updateOrder - updatedOrder order : {}", updatedOrder);


        // Envanter güncelleme isteğini Feign Client ile gönderiyoruz
        InventoryUpdateRequestDto updateRequest = getInventoryUpdateRequestDto(updatedOrder, updatedInventoryQuantity);
        log.info("OrderResponseDto::updateOrder - updateRequest  : {}", updateRequest);

        // stok güncelle
        inventoryClientService.updateInventory(inventory.getId(), updateRequest);
        log.info("OrderService::updateOrder finish");

        // sipariş güncelledikten sonra ödeme var mı kontrol et ?
        // sipariş güncellendiğinde ödeme silinecek müşteriden tekrar ödeme beklenecek.


        return orderMapper.mapToOrderResponseDto(updatedOrder);
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


    private OrderResponseDto getSaveOrder(OrderRequestDto orderRequestDto, Product product, Address address) {
        orderRequestDto.setShippingAddress(address);
        Order saveOrder = orderMapper.mapToOrder(orderRequestDto);
        saveOrder.setTotalAmount((product.getPrice() * orderRequestDto.getQuantity()));
        saveOrder.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(saveOrder);

        log.info("OrderResponseDto::getSaveOrder - saveOrder  : {}", saveOrder);
        return orderMapper.mapToOrderResponseDto(saveOrder);
    }

    private OrderResponseDto fallbackCreateOrder(OrderRequestDto orderRequestDto, Throwable t) {
        log.error("Error creating order: ", t);
        return new OrderResponseDto(); // or any fallback response
    }

    private OrderResponseDto fallbackUpdateOrder(OrderRequestDto orderRequestDto, Throwable t) {
        log.error("Error update  order: ", t);
        return new OrderResponseDto(); // or any fallback response
    }


    private static InventoryUpdateRequestDto getInventoryUpdateRequestDto( Order save, int updatedInventoryQuantity) {
        return InventoryUpdateRequestDto.builder()
                .inventoryId(save.getInventoryId())
                .newQuantity(updatedInventoryQuantity)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

}

package com.example.spring.boot.service;

import com.example.spring.boot.dto.OrderRequestDto;
import com.example.spring.boot.dto.OrderResponseDto;
import com.example.spring.boot.exception.OrderNotFoundException;
import com.example.spring.boot.external.InventoryClientService;
import com.example.spring.boot.external.ProductClientService;
import com.example.spring.boot.mapper.OrderMapper;
import com.example.spring.boot.model.Inventory;
import com.example.spring.boot.model.Order;
import com.example.spring.boot.model.Product;
import com.example.spring.boot.publisher.OrderMessageSender;
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
    private final OrderMapper orderMapper;
    private final OrderMessageSender orderMessageSender;

    public List<OrderResponseDto> getAllOrders() {
        log.info("OrderService::getAllOrders started");

        List<Order> orderList = orderRepository.findAll();

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

    // sipariş oluşmadan önce product veritabanında mevuct mu kontrol et
    // sipariş oluştur
    // inventory-service ile iletişime geç stok kontrolü yap
    // stokda mevcut ise sipraiş başarılı.
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
            throw new RuntimeException("Yeterli stok mevcut değil !");
        }

        OrderResponseDto savedOrder = getSaveOrder(orderRequestDto, product);
        log.info("OrderResponseDto::createOrder - saved order: {}", savedOrder);

        Order mapToOrder = orderMapper.mapToOrder(savedOrder);
        // Envanter güncelleme mesajını RabbitMQ ile gönderiyoruz
        updatedInventory(orderRequestDto, inventory);

        log.info("OrderResponseDto::createOrder - mapToOrder : {}", mapToOrder);
        log.info("Order::createOrder finished.");
        return orderMapper.mapToOrderResponseDto(mapToOrder);
    }

    public OrderResponseDto fallbackCreateOrder(OrderRequestDto orderRequestDto, Throwable t) {
        log.error("Error creating order: ", t);
        return new OrderResponseDto(); // or any fallback response
    }

    @Transactional
    @Retry(name = "orderService", fallbackMethod = "fallbackCreateOrder")
    @CircuitBreaker(name = "orderServiceBreaker", fallbackMethod = "fallbackCreateOrder")
    @RateLimiter(name = "createOrderLimiter", fallbackMethod = "fallbackCreateOrder")
    public OrderResponseDto updateOrder(String id, OrderRequestDto orderRequestDto) {
        log.info("OrderService::updateOrder started");

        OrderResponseDto existingOrder = getOrderById(id);
        Inventory inventory = inventoryClientService.getInventoryById(orderRequestDto.getInventoryId());

        log.info("OrderResponseDto::updateOrder - existingOrder with order id : {}", existingOrder.getId());
        log.info("OrderResponseDto::updateOrder - inventory with id : {}", inventory.getId());

        existingOrder.setOrderStatus(orderRequestDto.getOrderStatus());
        existingOrder.setQuantity(orderRequestDto.getQuantity());
        existingOrder.setShippingAddress(orderRequestDto.getShippingAddress());

        Order mapToOrder = orderMapper.mapToOrder(existingOrder);
        log.info("OrderResponseDto::updateOrder - mapToOrder  : {}", mapToOrder);

        Order save = orderRepository.save(mapToOrder);
        log.info("OrderResponseDto::updateOrder - save order : {}", save);


        // Envanter güncelleme mesajını RabbitMQ ile gönderiyoruz
        updatedInventory(orderRequestDto, inventory);
        log.info("OrderService::updateOrder finish");

        return orderMapper.mapToOrderResponseDto(save);
    }

    public void deleteOrder(String id) {
        log.info("OrderService::deleteOrder started");

        OrderResponseDto orderResponseDto = getOrderById(id);
        Order order = orderMapper.mapToOrder(orderResponseDto);

        orderRepository.delete(order);

        log.info("OrderService::deleteOrder finish");
    }


    private void updatedInventory(OrderRequestDto orderRequestDto, Inventory inventory) {
        // Envanter güncelleme işlemini RabbitMQ kullanarak mesaj kuyruğuna gönderiyoruz

        inventory.setStockQuantity(inventory.getStockQuantity() - orderRequestDto.getQuantity());
        // rabbit mq ile inventory stock güncelle.
        orderMessageSender.sendInventoryUpdateMessage(inventory);
    }

    private OrderResponseDto getSaveOrder(OrderRequestDto orderRequestDto, Product product) {
        orderRequestDto.setProductId(product.getId());
        orderRequestDto.setInventoryId(product.getInventoryId());
        orderRequestDto.setOrderDate(LocalDateTime.now());
        orderRequestDto.setTotalAmount((product.getPrice() * orderRequestDto.getQuantity()));

        Order saveOrder = orderMapper.mapToOrder(orderRequestDto);
        orderRepository.save(saveOrder);

        return orderMapper.mapToOrderResponseDto(saveOrder);
    }


}

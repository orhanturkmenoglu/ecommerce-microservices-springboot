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
import com.example.spring.boot.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClientService inventoryClientService;
    private final ProductClientService productClientService;

    private final OrderMapper orderMapper;

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
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Order::createOrder started");

        Product product = productClientService.getProductById(orderRequestDto.getProductId());
        Inventory inventory = inventoryClientService.getInventoryById(orderRequestDto.getInventoryId());

        if (Objects.isNull(product)) {
            throw new NullPointerException("Product with not found id :" + product.getId());
        }

        if (Objects.isNull(inventory)) {
            throw new NullPointerException("Inventory with not found id :" + inventory.getId());
        }

        // stok kontrolü yap.
        // sipariş oluştur
        if (!(inventory.getStockQuantity() >= orderRequestDto.getQuantity())) {
            throw new RuntimeException("Yeterli stok mevcut değil !");
        }

        OrderResponseDto savedOrder = getSaveOrder(orderRequestDto, product);
        Order mapToOrder = orderMapper.mapToOrder(savedOrder);


        updatedInventory(orderRequestDto, inventory);

        log.info("Order::createOrder finished.");
        return orderMapper.mapToOrderResponseDto(mapToOrder);
    }

    @Transactional
    public OrderResponseDto updateOrder(String id, OrderRequestDto orderRequestDto) {
        log.info("OrderService::updateOrder started");

        OrderResponseDto existingOrder = getOrderById(id);

        existingOrder.setOrderStatus(orderRequestDto.getOrderStatus());
        existingOrder.setTotalAmount(orderRequestDto.getTotalAmount());
        existingOrder.setShippingAddress(orderRequestDto.getShippingAddress());

        Order updateOrder = orderMapper.mapToOrder(existingOrder);
        Order save = orderRepository.save(updateOrder);

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
        // inventory - service ile iletişime geç stok güncelle.
        inventory.setStockQuantity(inventory.getStockQuantity() - orderRequestDto.getQuantity());
        inventoryClientService.updateInventory(inventory);
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

package com.example.spring.boot.service;

import com.example.spring.boot.dto.inventoryDto.InventoryUpdateRequestDto;
import com.example.spring.boot.dto.orderDto.OrderRequestDto;
import com.example.spring.boot.dto.orderDto.OrderResponseDto;
import com.example.spring.boot.dto.orderDto.OrderUpdateRequestDto;
import com.example.spring.boot.enums.Category;
import com.example.spring.boot.enums.OrderStatus;
import com.example.spring.boot.exception.InventoryNotFoundException;
import com.example.spring.boot.exception.OrderNotFoundException;
import com.example.spring.boot.exception.ProductNotFoundException;
import com.example.spring.boot.external.*;
import com.example.spring.boot.mapper.OrderMapper;
import com.example.spring.boot.model.*;
import com.example.spring.boot.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClientService inventoryClientService;

    @Mock
    private ProductClientService productClientService;

    @Mock
    private CustomerClientService customerClientService;

    @Mock
    private CargoClientService cargoClientService;

    @Mock
    private PaymentClientService paymentClientService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderRequestDto orderRequestDto;
    private OrderResponseDto orderResponseDto;
    private OrderUpdateRequestDto orderUpdateRequestDto;
    private Product product;
    private Inventory inventory;
    private Customer customer;
    private Address address;
    private Order order2;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void setUp() {

        customer = new Customer();
        customer.setId("CUSTOMER123");
        customer.setFirstName("Orhan");
        customer.setLastName("TÜRKMENOĞLU");
        customer.setPhoneNumber("0555 555 55 55");

        address = new Address();
        address.setCity("HATAY");
        address.setCountry("TURKEY");
        address.setDistrict("ANTAKYA");
        address.setStreet("Ülgen Paşa Caddesi");
        address.setZipCode("31440");

        customer.setAddressList(List.of(address));

        order = new Order();
        order.setId("ORDER123");
        order.setCustomerId("CUSTOMER123");
        order.setProductId("PRODUCT123");
        order.setInventoryId("INVENTORY123");
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setTotalAmount(1200.00);
        order.setOrderDate(LocalDateTime.now());
        order.setQuantity(2);
        order.setShippingAddress(address);

        order2 = new Order();
        order2.setId("ORDER456");

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setCustomerId(order.getCustomerId());
        orderRequestDto.setInventoryId(order.getInventoryId());
        orderRequestDto.setProductId(order.getProductId());
        orderRequestDto.setShippingAddress(order.getShippingAddress());
        orderRequestDto.setQuantity(order.getQuantity());

        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(order.getId());
        orderResponseDto.setCustomerId(order.getCustomerId());
        orderResponseDto.setProductId(order.getProductId());
        orderResponseDto.setInventoryId(order.getInventoryId());
        orderResponseDto.setOrderStatus(order.getOrderStatus());
        orderResponseDto.setTotalAmount(order.getTotalAmount());
        orderResponseDto.setOrderDate(order.getOrderDate());
        orderResponseDto.setQuantity(order.getQuantity());
        orderResponseDto.setShippingAddress(order.getShippingAddress());


        product = new Product();
        product.setId("PRODUCT123");
        product.setName("MASA");
        product.setPrice(600);
        product.setDescription("ÇOCUK ÇALIŞMA MASASI");
        product.setCategory(Category.EV_YAŞAM);

        inventory = new Inventory();
        inventory.setId("INVENTORY123");
        inventory.setProductId(product.getId());
        inventory.setStockQuantity(10);

        product.setInventoryId(inventory.getId());

    }

    @Test
    public void createOrder_ShouldReturnOrderResponseDto_WhenOrderIsCreated() throws Exception {
        when(productClientService.getProductById(product.getId())).thenReturn(product);
        when(inventoryClientService.getInventoryById(inventory.getId())).thenReturn(inventory);
        when(customerClientService.getCustomerById(customer.getId())).thenReturn(customer);
        when(orderMapper.mapToOrder(orderRequestDto)).thenReturn(order);
        when(orderMapper.mapToOrderResponseDto(order)).thenReturn(orderResponseDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDto result = orderService.createOrder(orderRequestDto);

        assert result != null;
        assert result.getId().equals(order.getId());
        assert result.getCustomerId().equals(order.getCustomerId());
        assert result.getProductId().equals(order.getProductId());
        assert result.getInventoryId().equals(order.getInventoryId());
        assert result.getOrderStatus().equals(order.getOrderStatus());
        assert Objects.equals(result.getTotalAmount(), order.getTotalAmount());
        assert result.getOrderDate().equals(order.getOrderDate());
        assert Objects.equals(result.getQuantity(), order.getQuantity());
        assert result.getShippingAddress().equals(order.getShippingAddress());


        verify(orderRepository, times(1)).save(any(Order.class));

    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {
        when(productClientService.getProductById(product.getId())).thenThrow(new ProductNotFoundException("Product not found"));

        Exception exception = assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(orderRequestDto));

        assertEquals("Product not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenInventoryNotFound() {
        when(productClientService.getProductById(product.getId())).thenReturn(product);
        when(inventoryClientService.getInventoryById(inventory.getId())).thenThrow(new InventoryNotFoundException("Inventory not found"));

        Exception exception = assertThrows(InventoryNotFoundException.class, () -> orderService.createOrder(orderRequestDto));

        assertEquals("Inventory not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenOrderNotSaved() {
        when(productClientService.getProductById(product.getId())).thenReturn(product);
        when(inventoryClientService.getInventoryById(inventory.getId())).thenReturn(inventory);
        when(customerClientService.getCustomerById(customer.getId())).thenReturn(customer);
        when(orderMapper.mapToOrder(orderRequestDto)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Order could not be saved"));

        Exception exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(orderRequestDto));

        assertEquals("Order could not be saved", exception.getMessage());
    }

    @Test
    void getOrderById_ShouldReturnOrderResponseDto_WhenOrderExists() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.mapToOrderResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.getOrderById(order.getId());

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getCustomerId(), result.getCustomerId());
        assertEquals(order.getProductId(), result.getProductId());
        assertEquals(order.getInventoryId(), result.getInventoryId());
        assertEquals(order.getOrderStatus(), result.getOrderStatus());
        assertEquals(order.getTotalAmount(), result.getTotalAmount());
        assertEquals(order.getQuantity(), result.getQuantity());
        assertEquals(order.getShippingAddress(), result.getShippingAddress());

        verify(orderRepository, times(1)).findById("ORDER123");
        verify(orderMapper, times(1)).mapToOrderResponseDto(order);
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findById("ORDER999")).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById("ORDER999"));

        verify(orderRepository, times(1)).findById("ORDER999");
        verify(orderMapper, never()).mapToOrderResponseDto(any());
    }

    @Test
    void getByOrderDateBetween_ShouldReturnOrdersWithinDateRange() {
        // Arrange
        String startDateTime = "2025-03-01 00:00:00";
        String endDateTime = "2025-03-23 23:59:59";


        order.setOrderDate(LocalDateTime.parse("2025-03-10 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        order2.setOrderDate(LocalDateTime.parse("2025-03-15 14:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        List<Order> orders = List.of(order, order2);

        when(orderRepository.findByOrderDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);

        // Act
        List<OrderResponseDto> result = orderService.getByOrderDateBetween(startDateTime, endDateTime);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.get(0).getId());
        assertEquals(order2.getId(), result.get(1).getId());
        assertTrue(result.get(0).getOrderDate().isAfter(LocalDateTime.parse("2025-03-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        assertTrue(result.get(1).getOrderDate().isBefore(LocalDateTime.parse("2025-03-23 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        verify(orderRepository, times(1)).findByOrderDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrderResponseDto_WhenOrdersExist() {
        // Given
        List<Order> orderList = List.of(order);
        List<OrderResponseDto> responseDtoList = List.of(orderResponseDto);

        when(orderRepository.findAll()).thenReturn(orderList);
        when(orderMapper.mapToOrderResponseDtoList(orderList)).thenReturn(responseDtoList);

        // When
        List<OrderResponseDto> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).mapToOrderResponseDtoList(orderList);
    }

    @Test
    void getAllOrders_ShouldReturnEmptyList_WhenNoOrdersExist() {
        // Arrange
        List<Order> emptyOrderList = Collections.emptyList();
        when(orderRepository.findAll()).thenReturn(emptyOrderList);
        when(orderMapper.mapToOrderResponseDtoList(emptyOrderList)).thenReturn(Collections.emptyList());

        // Actual
        List<OrderResponseDto> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).mapToOrderResponseDtoList(emptyOrderList);
    }

    @Test
    void updateOrder_ShouldUpdateOrder_WhenValidData() {
        // Arrange
        String orderId = "ORDER123";
        String paymentId = "PAYMENT123";  // Add a valid paymentId here
        OrderResponseDto existingOrder = new OrderResponseDto();
        existingOrder.setId(orderId);
        existingOrder.setQuantity(2);
        existingOrder.setTotalAmount(1200.00);

        OrderUpdateRequestDto orderUpdateRequestDto = new OrderUpdateRequestDto();
        orderUpdateRequestDto.setInventoryId("INVENTORY123");
        orderUpdateRequestDto.setProductId("PRODUCT123");
        orderUpdateRequestDto.setCustomerId("CUSTOMER123");
        orderUpdateRequestDto.setQuantity(3);
        orderUpdateRequestDto.setPaymentId(paymentId);  // Add the paymentId

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(10);

        Product product = new Product();
        product.setPrice(600);

        Customer customer = new Customer();
        customer.setId("CUSTOMER123");
        Address address = new Address();
        address.setCity("HATAY");
        customer.setAddressList(List.of(address));

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setQuantity(orderUpdateRequestDto.getQuantity());
        updatedOrder.setTotalAmount(product.getPrice() * orderUpdateRequestDto.getQuantity());
        updatedOrder.setShippingAddress(address);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));
        when(orderMapper.mapToOrderResponseDto(any(Order.class))).thenReturn(existingOrder);
        when(orderMapper.mapToOrder(any(OrderResponseDto.class))).thenReturn(updatedOrder);
        when(inventoryClientService.getInventoryById(anyString())).thenReturn(inventory);
        when(productClientService.getProductById(anyString())).thenReturn(product);
        when(customerClientService.getCustomerById(anyString())).thenReturn(customer);
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // Envanter güncelleme talebini mock'la
        InventoryUpdateRequestDto updateRequest = new InventoryUpdateRequestDto();
        updateRequest.setNewQuantity(inventory.getStockQuantity() + 2);  // Adjust this according to the logic in your service

        // Act
        OrderResponseDto result = orderService.updateOrder(orderId, orderUpdateRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(3, result.getQuantity());
        assertEquals(1800.00, result.getTotalAmount());
        assertNotNull(result.getShippingAddress());

        // Verify cancelPayment is called with the correct paymentId
        verify(paymentClientService, times(1)).cancelPayment(paymentId);

        // Verify that updateInventory is called with correct parameters
    }

    @Test
    void deleteOrder_ShouldDeleteOrder_WhenOrderExists() {
        // Given
        when(orderService.getOrderById(order.getId())).thenReturn(orderResponseDto);
        when(orderMapper.mapToOrder(orderResponseDto)).thenReturn(order);

        // When
        orderService.deleteOrder(order.getId());

        // Then
        verify(orderService, times(1)).getOrderById(order.getId());
        verify(orderMapper, times(1)).mapToOrder(orderResponseDto);
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void deleteOrder_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {

        // Arrange
        order.setId(null);

        // Actual
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(order.getId()));

        // Assert
        assertEquals("Order not found for ID: " + order.getId(), exception.getMessage());

        verify(orderRepository, never()).delete(any(Order.class)); // Silme işlemi yapılmamalı!
    }

}

package com.example.spring.boot.controller;

import com.example.spring.boot.dto.orderDto.OrderRequestDto;
import com.example.spring.boot.dto.orderDto.OrderResponseDto;
import com.example.spring.boot.dto.orderDto.OrderUpdateRequestDto;
import com.example.spring.boot.enums.Category;
import com.example.spring.boot.enums.OrderStatus;
import com.example.spring.boot.model.*;
import com.example.spring.boot.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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

        orderUpdateRequestDto = new OrderUpdateRequestDto();
        orderUpdateRequestDto.setCustomerId(orderResponseDto.getCustomerId());
        orderUpdateRequestDto.setProductId(orderResponseDto.getProductId());
        orderUpdateRequestDto.setInventoryId(orderResponseDto.getInventoryId());
        orderUpdateRequestDto.setPaymentId("PAYMENT123");
        orderUpdateRequestDto.setOrderDate(orderResponseDto.getOrderDate());
        orderUpdateRequestDto.setQuantity(orderResponseDto.getQuantity());
        orderUpdateRequestDto.setShippingAddress(orderResponseDto.getShippingAddress());

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
    void getAllOrders_ShouldReturnOrderList_WhenOrderListExists() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(orderResponseDto));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getOrderById_ShouldReturnOrderResponseDto_WhenOrderByIdExists() throws Exception {
        when(orderService.getOrderById(order.getId())).thenReturn(orderResponseDto);

        mockMvc.perform(get("/api/v1/orders/{id}",order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void createOrder_ShouldReturnOrderResponseDto_WhenOrderCreatedSuccessfully() throws Exception {
        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(orderResponseDto);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderResponseDto.getId()));
    }

    @Test
    void updateOrder_ShouldReturnOrderResponseDto_WhenOrderUpdatedSuccessfully() throws Exception {
        when(orderService.updateOrder(any(String.class),any(OrderUpdateRequestDto.class))).thenReturn(orderResponseDto);

        mockMvc.perform(put("/api/v1/orders/{id}", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderUpdateRequestDto)))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk());
    }
    @Test
    void deleteOrder_ShouldReturnNoContent_WhenOrderDeletedSuccessfully() throws Exception {

        mockMvc.perform(delete("/api/v1/orders/{id}",order.getId()))
                .andExpect(status().isNoContent());
    }
}

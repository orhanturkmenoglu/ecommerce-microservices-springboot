package com.example.payment_service.controller;

import com.example.payment_service.dto.orderDto.OrderResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.enums.PaymentType;
import com.example.payment_service.model.Customer;
import com.example.payment_service.model.Inventory;
import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;


    private Payment payment;
    private PaymentRequestDto paymentRequestDto;
    private PaymentUpdateRequestDto paymentUpdateRequestDto;
    private PaymentResponseDto paymentResponseDto;
    private OrderResponseDto order;
    private Inventory inventory;
    private Customer customer;

    @BeforeEach
    public void setUp(){

        order = new OrderResponseDto();
        order.setId("ORDER123");
        order.setInventoryId("inventory123");
        order.setQuantity(2);
        order.setTotalAmount(100.0);

        inventory = new Inventory();
        inventory.setId("inventory123");

        customer = new Customer();
        customer.setId("CUSTOMER123");

        payment = new Payment();
        payment.setId("PAYMENT123");
        payment.setOrderId("ORDER123");
        payment.setCargoId("CARGO123");
        payment.setCustomerId("CUSTOMER123");
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(100.0);
        payment.setPaymentType(PaymentType.CREDIT_CARD);
        payment.setQuantity(2);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCheckOutUrl("https://stripe.com/checkout/1234");
        payment.setStripePaymentStatus("paid");

        paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setOrderId("ORDER123");
        paymentRequestDto.setCargoId("CARGO123");
        paymentRequestDto.setCustomerId("CUSTOMER123");
        paymentRequestDto.setPaymentType(PaymentType.CREDIT_CARD);

        paymentUpdateRequestDto = new PaymentUpdateRequestDto();
        paymentUpdateRequestDto.setOrderId("ORDER123");
        paymentUpdateRequestDto.setAmount(1200.0);
        paymentUpdateRequestDto.setPaymentType(PaymentType.CREDIT_CARD);

        paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setId(payment.getId());
        paymentResponseDto.setCustomerId(payment.getCustomerId());
        paymentResponseDto.setOrderId(payment.getOrderId());
        paymentResponseDto.setCargoId(payment.getCargoId());
        paymentResponseDto.setQuantity(payment.getQuantity());
        paymentResponseDto.setAmount(payment.getAmount());
        paymentResponseDto.setPaymentStatus(payment.getPaymentStatus());
        paymentResponseDto.setPaymentType(payment.getPaymentType());
        paymentResponseDto.setPaymentDate(payment.getPaymentDate());
        paymentResponseDto.setStripePaymentStatus(payment.getStripePaymentStatus());
        paymentResponseDto.setCheckoutUrl(payment.getCheckOutUrl());

    }


    @Test
    void getPaymentById_ShouldReturnPaymentResponseDto_WhenPaymentByIdExists() throws Exception {
        when(paymentService.getPaymentById(payment.getId())).thenReturn(paymentResponseDto);

        mockMvc.perform(get("/api/v1/payments/{id}", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId()));
    }

    @Test
    void createPayment_ShouldReturnPaymentResponseDto_WhenPaymentCreatedSuccessfully() throws Exception {
        when(paymentService.processPayment(any(PaymentRequestDto.class))).thenReturn(paymentResponseDto);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(paymentResponseDto.getId()));
    }

    @Test
    void updatePayment_ShouldReturnPaymentResponseDto_WhenPaymentUpdatedSuccessfully() throws Exception {
        when(paymentService.updatePayment(any(PaymentUpdateRequestDto.class))).thenReturn(paymentResponseDto);

        mockMvc.perform(put("/api/v1/payments/{id}", payment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentResponseDto.getId()));
    }

    @Test
    void deletePayment_ShouldReturnNoContent_WhenPaymentDeletedSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/payments/{id}", payment.getId()))
                .andExpect(status().isNoContent());
    }
}

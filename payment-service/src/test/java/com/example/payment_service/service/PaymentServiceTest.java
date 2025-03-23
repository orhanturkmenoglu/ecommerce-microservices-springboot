package com.example.payment_service.service;

import com.example.payment_service.dto.orderDto.OrderResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentRequestDto;
import com.example.payment_service.dto.paymentDto.PaymentResponseDto;
import com.example.payment_service.dto.paymentDto.PaymentUpdateRequestDto;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.enums.PaymentType;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.external.CargoClientService;
import com.example.payment_service.external.CustomerClientService;
import com.example.payment_service.external.InventoryServiceClient;
import com.example.payment_service.external.OrderServiceClient;
import com.example.payment_service.mapper.PaymentMapper;
import com.example.payment_service.model.Customer;
import com.example.payment_service.model.Inventory;
import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;
import com.example.payment_service.util.PaymentMessage;
import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @Mock
    private CustomerClientService customerClientService;

    @Mock
    private Stripe stripeClient;

    @Mock
    private PaymentMapper paymentMapper;

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
    void testCreateCheckoutSession() throws StripeException {
        // Arrange
        Long amount = 1000L;  // 10.00 USD (Stripe cinsinden en küçük birim)
        String currency = "usd";
        Long quantity = 1L;
        String productName = "Test Product";

        // Stripe'ın Session.create() metodunun mocklanması
        Session mockSession = mock(Session.class);
        when(mockSession.getUrl()).thenReturn("http://localhost:8085/checkout");
        when(mockSession.getPaymentStatus()).thenReturn("unpaid");

        // Session.create() metodunu mockluyoruz.
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            // Act
            String[] sessionDetails = paymentService.createCheckoutSession(amount, currency, quantity, productName);

            // Assert
            assertNotNull(sessionDetails);
            assertEquals("http://localhost:8085/checkout", sessionDetails[0]);  // Checkout URL doğrulaması
            assertEquals("unpaid", sessionDetails[1]);  // Payment Status doğrulaması
        }
    }


    @Test
    public void getPaymentByID_ShouldReturnPaymentResponseDto_WhenPaymentExists(){
        // Arrange
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(paymentMapper.mapToPaymentResponseDto(payment)).thenReturn(paymentResponseDto);

        // Actual
        PaymentResponseDto response = paymentService.getPaymentById(this.payment.getId());

        // Assert
        assert response != null;
        assert response.getId().equals(payment.getId());
        assert response.getCustomerId().equals(payment.getCustomerId());
        assert response.getOrderId().equals(payment.getOrderId());
        assert response.getCargoId().equals(payment.getCargoId());
        assert Objects.equals(response.getQuantity(), payment.getQuantity());
        assert response.getAmount().equals(payment.getAmount());
        assert response.getPaymentStatus().equals(payment.getPaymentStatus());
        assert response.getPaymentType().equals(payment.getPaymentType());
        assert response.getPaymentDate().equals(payment.getPaymentDate());
        assert response.getStripePaymentStatus().equals(payment.getStripePaymentStatus());
        assert response.getCheckoutUrl().equals(payment.getCheckOutUrl());

        // verify
        verify(paymentRepository,times(1)).findById(payment.getId());
        verify(paymentMapper,times(1)).mapToPaymentResponseDto(payment);
    }

    @Test
    public void getPaymentByID_ShouldThrowPaymentNotFoundException_WhenPaymentDoesNotExist(){
        // Arrange
        payment.setId(null);

        // Actual & Assert
        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(payment.getId()));


        // Assert
        assert exception != null;
        assert exception.getMessage().equals(PaymentMessage.PAYMENT_NOT_FOUND + payment.getId());

        // verify
        verify(paymentRepository,times(1)).findById(payment.getId());
    }

    @Test
    void testGetPaymentType_ValidPaymentType() {
        // Arrange
        String paymentType = "CREDIT_CARD";
        PaymentType paymentTypeEnum = PaymentType.CREDIT_CARD;

        Payment payment1 = new Payment();
        payment1.setPaymentType(paymentTypeEnum);

        Payment payment2 = new Payment();
        payment2.setPaymentType(paymentTypeEnum);

        List<Payment> paymentList = Arrays.asList(payment1, payment2);

        when(paymentRepository.findByPaymentType(paymentTypeEnum)).thenReturn(paymentList);

        PaymentResponseDto responseDto = new PaymentResponseDto();
        when(paymentMapper.maptoPaymentResponseDtoList(paymentList)).thenReturn(Collections.singletonList(responseDto));

        // Act
        List<PaymentResponseDto> result = paymentService.getPaymentType(paymentType);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Assuming that paymentMapper returns a list with one element
        verify(paymentRepository).findByPaymentType(paymentTypeEnum);
        verify(paymentMapper).maptoPaymentResponseDtoList(paymentList);
    }

    @Test
    void testGetPaymentType_InvalidPaymentType() {
        // Arrange
        String invalidPaymentType = "INVALID_TYPE";

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.getPaymentType(invalidPaymentType);
        });

        assertEquals("Invalid payment type: INVALID_TYPE", thrown.getMessage());
        verify(paymentRepository, never()).findByPaymentType(any());
        verify(paymentMapper, never()).maptoPaymentResponseDtoList(any());
    }

    @Test
    void testGetPaymentDateBetween_ValidDates() {
        // Arrange
        String startDate = "2025-03-01 00:00:00";
        String endDate = "2025-03-10 23:59:59";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        Payment payment1 = new Payment();
        payment1.setPaymentDate(LocalDateTime.of(2025, 3, 5, 10, 30, 0, 0));

        Payment payment2 = new Payment();
        payment2.setPaymentDate(LocalDateTime.of(2025, 3, 7, 15, 45, 0, 0));

        List<Payment> paymentList = Arrays.asList(payment1, payment2);

        when(paymentRepository.findByPaymentDateBetween(start, end)).thenReturn(paymentList);

        PaymentResponseDto responseDto = new PaymentResponseDto();
        when(paymentMapper.maptoPaymentResponseDtoList(paymentList)).thenReturn(Collections.singletonList(responseDto));

        // Act
        List<PaymentResponseDto> result = paymentService.getPaymentDateBetween(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Assuming paymentMapper returns a list with one element
        verify(paymentRepository).findByPaymentDateBetween(start, end);
        verify(paymentMapper).maptoPaymentResponseDtoList(paymentList);
    }

    @Test
    void testGetPaymentDateBetween_InvalidDateFormat() {
        // Arrange
        String invalidStartDate = "2025-03-01 00:00"; // Invalid format (missing seconds)
        String invalidEndDate = "2025-03-10 23:59"; // Invalid format (missing seconds)

        // Act & Assert
        assertThrows(DateTimeParseException.class, () -> {
            paymentService.getPaymentDateBetween(invalidStartDate, invalidEndDate);
        });

        verify(paymentRepository, never()).findByPaymentDateBetween(any(), any());
        verify(paymentMapper, never()).maptoPaymentResponseDtoList(any());
    }
    @Test
    void testGetPaymentCustomerById_ShouldReturnPaymentResponseDto_ValidCustomerId() {
        // Arrange
        String customerId = "12345";
        Payment paymentCustomer = new Payment();
        paymentCustomer.setCustomerId(customerId);
        paymentCustomer.setAmount(100.0); // Example payment details

        when(paymentRepository.findByCustomerId(customerId)).thenReturn(Optional.of(paymentCustomer));

        PaymentResponseDto responseDto = new PaymentResponseDto();
        when(paymentMapper.mapToPaymentResponseDto(paymentCustomer)).thenReturn(responseDto);

        // Act
        PaymentResponseDto result = paymentService.getPaymentCustomerById(customerId);

        // Assert
        assertNotNull(result);
        verify(paymentRepository).findByCustomerId(customerId);
        verify(paymentMapper).mapToPaymentResponseDto(paymentCustomer);
    }

    @Test
    void testGetPaymentCustomerById_ShouldThrowNullPointerException_CustomerNotFound() {
        // Arrange
        String customerId = "12345";

        when(paymentRepository.findByCustomerId(customerId)).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            paymentService.getPaymentCustomerById(customerId);
        });

        verify(paymentRepository).findByCustomerId(customerId);
        verify(paymentMapper, never()).mapToPaymentResponseDto(any());
    }


    @Test
    void testCancelPaymentById_Success() {
        // Arrange
        String paymentId = "payment123";

        // Mock the payment repository to return a payment when findById is called
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentStatus(PaymentStatus.PENDING); // Assume it's in a pending state

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        paymentService.cancelPaymentById(paymentId);

        // Assert
        assertEquals(PaymentStatus.REFUNDED, payment.getPaymentStatus());
        verify(paymentRepository).save(payment); // Ensure save was called to update the payment status
        verify(paymentRepository).findById(paymentId); // Ensure findById was called to fetch the payment
    }

    @Test
    void testCancelPaymentById_PaymentNotFound() {
        // Arrange
        String paymentId = "invalidPaymentId";

        // Mock the payment repository to return an empty Optional for the invalid payment ID
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.cancelPaymentById(paymentId);
        });

        verify(paymentRepository).findById(paymentId); // Ensure findById was called
        verify(paymentRepository, never()).save(any()); // Ensure save was not called since payment was not found
    }

    @Test
    void testDeleteByPaymentId_Success() {
        // Arrange
        String paymentId = "payment123";

        // Mock the payment repository to return a payment when getPayment is called
        Payment payment = new Payment();
        payment.setId(paymentId);

        // Mock the getPayment method to return the payment
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        paymentService.deleteByPaymentId(paymentId);

        // Assert
        verify(paymentRepository).deleteById(paymentId); // Ensure deleteById was called to delete the payment
    }

    @Test
    void testDeleteByPaymentId_PaymentNotFound() {
        // Arrange
        String paymentId = "invalidPaymentId";

        // Mock the payment repository to return an empty Optional for the invalid payment ID
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.deleteByPaymentId(paymentId);
        });

        verify(paymentRepository).findById(paymentId); // Ensure findById was called to fetch the payment
        verify(paymentRepository, never()).deleteById(any()); // Ensure deleteById was not called since payment was not found
    }

    @Test
    public void testPaymentServiceFallback_WithReflection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        Exception exception = new Exception("Service is down");

        Method method = PaymentService.class.getDeclaredMethod("paymentServiceFallback", Exception.class);

        method.setAccessible(true);

        // Act
        PaymentResponseDto result =(PaymentResponseDto) method.invoke(paymentService, exception);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getOrderId());
        assertEquals(0.0, result.getAmount());
        assertEquals(PaymentStatus.FAILED, result.getPaymentStatus());
        assertEquals(1, result.getQuantity());
    }

}

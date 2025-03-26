package com.example.customer_service.controller;

import com.example.customer_service.dto.customerDto.CustomerRequestDto;
import com.example.customer_service.dto.customerDto.CustomerResponseDto;
import com.example.customer_service.dto.customerDto.CustomerUpdateRequestDto;
import com.example.customer_service.enums.Country;
import com.example.customer_service.external.CargoClientService;
import com.example.customer_service.model.Address;
import com.example.customer_service.model.Customer;
import com.example.customer_service.service.CustomerService;
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

@WebMvcTest(CustomerController.class)
@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CargoClientService cargoClientService;

    private CustomerRequestDto customerRequestDto;
    private CustomerUpdateRequestDto customerUpdateRequestDto;
    private CustomerResponseDto customerResponseDto;
    private Customer customer;
    private Address address;

    @BeforeEach
    public void setUp() {

        // Address nesnesi oluşturuluyor
        address = new Address();
        address.setId("ADDRES1234");
        address.setCity("HATAY");
        address.setDistrict("Antakya");
        address.setZipCode("31440");
        address.setStreet("Gazi Mahallesi");
        address.setCountry(Country.TURKEY);
        address.setCreatedDate(LocalDateTime.now());
        address.setDescription("Ev Adresi");

        // Müşteri nesnesi oluşturuluyor
        customer = new Customer();
        customer.setId("CUST1234");
        customer.setFirstName("Orhan");
        customer.setLastName("TÜRKMENOĞLU");
        customer.setPhoneNumber("5555555555");
        customer.setEmail("orhanturkmenoglu1@gmail.com");
        customer.setCreatedDate(LocalDateTime.now());
        customer.setAddressList(List.of(address));

        // CustomerRequestDto nesnesi oluşturuluyor
        customerRequestDto = new CustomerRequestDto();
        customerRequestDto.setFirstName(customer.getFirstName());
        customerRequestDto.setLastName(customer.getLastName());
        customerRequestDto.setPhoneNumber(customer.getPhoneNumber());
        customerRequestDto.setEmail(customer.getEmail());
        customerRequestDto.setCreatedDate(customer.getCreatedDate());
        customerRequestDto.setAddressList(List.of(address));

        // CustomerUpdateRequestDto hazırlanıyor
        customerUpdateRequestDto = new CustomerUpdateRequestDto();
        customerUpdateRequestDto.setFirstName("Orhan");
        customerUpdateRequestDto.setLastName("TÜRKMENOĞLU");
        customerUpdateRequestDto.setPhoneNumber("5555555555");
        customerUpdateRequestDto.setEmail("orhanturkmenoglu13@gmail.com");
        customerUpdateRequestDto.setCreatedDate(LocalDateTime.now());

        // CustomerResponseDto oluşturuluyor
        customerResponseDto = new CustomerResponseDto();
        customerResponseDto.setId(customer.getId());
        customerResponseDto.setFirstName(customer.getFirstName());
        customerResponseDto.setLastName(customer.getLastName());
        customerResponseDto.setPhoneNumber(customer.getPhoneNumber());
        customerResponseDto.setEmail(customer.getEmail());
        customerResponseDto.setCreatedDate(customer.getCreatedDate());
    }

    @Test
    public void createCustomer_ShouldReturnCustomerResponseDto_WhenCustomerExists() throws Exception {
        when(customerService.createCustomer(any(CustomerRequestDto.class))).thenReturn(customerResponseDto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customerResponseDto.getId()))
                .andExpect(jsonPath("$.email").value(customerResponseDto.getEmail()));
    }

    @Test
    public void getCustomersAll_ShouldReturnCustomerList_WhenCustomerExist() throws Exception {
        when(customerService.getCustomersAll()).thenReturn(List.of(customerResponseDto));

        mockMvc.perform(get("/api/v1/customers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(customerResponseDto.getId()));
    }

    @Test
    public void getCustomerById_ShouldReturnCustomerResponseDto_WhenCustomerExists() throws Exception {
        when(customerService.getCustomerById("CUST1234")).thenReturn(customerResponseDto);

        mockMvc.perform(get("/api/v1/customers/{customerId}", "CUST1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerResponseDto.getId()));
    }

    @Test
    public void getCustomersByFirstName_ShouldReturnListOfCustomerResponseDto_WhenCustomerExists() throws Exception {
        when(customerService.getCustomersByFirstName("Orhan")).thenReturn(List.of(customerResponseDto));

        mockMvc.perform(get("/api/v1/customers/customerByFirstName")
                        .param("firstName", "Orhan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Orhan"));
    }

    @Test
    public void updateCustomer_ShouldReturnCargoResponseDto_WhenCustomerUpdatedSuccessfully() throws Exception {

        when(customerService.updateCustomer(any(String.class), any(CustomerUpdateRequestDto.class)))
                .thenReturn(customerResponseDto);

        mockMvc.perform(put("/api/v1/customers/{customerId}", "CUST1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerUpdateRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCustomerById_ShouldReturnNoContent_WhenCustomerDeletedSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/{customerId}", "CUST1234"))
                .andExpect(status().isNoContent());
    }
}
